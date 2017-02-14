import pytest
import shakedown

import sdk_install as install
import sdk_plan as plan

from tests.config import (
    PACKAGE_NAME
)


def setup_module(module):
    shakedown.uninstall_package_and_data(PACKAGE_NAME, PACKAGE_NAME)
    options = {
        "service": {
            "spec_file": "examples/cni.yml"
        }
    }

    install.install(PACKAGE_NAME, 1, additional_options=options)


@pytest.mark.sanity
def test_deploy():
    """Verify that the current deploy plan matches the expected plan from the spec."""
    deployment_plan = plan.get_deployment_plan(PACKAGE_NAME).json()
    print("deployment_plan: " + str(deployment_plan))

    assert(len(deployment_plan['phases']) == 1)
    assert(deployment_plan['phases'][0]['name'] == 'all-deploy')
    assert(len(deployment_plan['phases'][0]['steps']) == 2)


@pytest.mark.sanity
def test_joins_overlay_network():
    """Verify that the container joined the dcos subnet at 9.0.0.0/24.

    The logic for this is in the task itself, which will check the container IP address
    and fail if incorrect, thus preventing the plan from reaching the COMPLETE state."""
    shakedown.wait_for(lambda: (
        plan.get_deployment_plan(PACKAGE_NAME).json()['status'] == 'COMPLETE'))
