package com.mesosphere.sdk.specification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mesosphere.sdk.offer.PortRangeRequirement;
import com.mesosphere.sdk.offer.ResourceRequirement;
import com.mesosphere.sdk.offer.ResourceUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.mesos.Protos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

/**
 * This class represents a single port, with associated environment name.
 */
public class PortSpec {
    private final Protos.Value value;
    @NotNull
    @Size(min = 1)
    private final String portName;
    private final String envKey;

    @JsonCreator
    public PortSpec(
            @JsonProperty("value") Protos.Value value,
            @JsonProperty("env-key") String envKey,
            @JsonProperty("port-name") String portName) {
        this.value = value;
        this.portName = portName;
        this.envKey = envKey;
    }

    @JsonProperty("value")
    public Protos.Value getValue() {
        return value;
    }

    @JsonProperty("port-name")
    public String getPortName() {
        return portName;
    }

    @JsonProperty("env-key")
    public Optional<String> getEnvKey() {
        return Optional.ofNullable(envKey);
    }

    public ResourceRequirement getResourceRequirement(Protos.Resource resource, ResourceSpec resourceSpec) {
        Protos.Resource portResource = resource == null ?
                ResourceUtils.getDesiredResource(
                        resourceSpec.getRole(),
                        resourceSpec.getPrincipal(),
                        resourceSpec.getName(),
                        getValue()) :
                ResourceUtils.withValue(resource, getValue());
        Protos.Value.Range range = getValue().getRanges().getRange(0);
        return new PortRangeRequirement(
                portResource,
                getPortName(),
                (int) range.getBegin(),
                (int) range.getEnd(),
                getEnvKey());
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
