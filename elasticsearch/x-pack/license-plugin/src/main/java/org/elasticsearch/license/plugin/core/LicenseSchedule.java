/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.license.plugin.core;

import org.elasticsearch.license.core.License;
import org.elasticsearch.xpack.scheduler.SchedulerEngine;

import static org.elasticsearch.license.plugin.core.LicensesService.GRACE_PERIOD_DURATION;
import static org.elasticsearch.license.plugin.core.LicensesService.getLicenseState;

public class LicenseSchedule implements SchedulerEngine.Schedule {

    private final License license;

    LicenseSchedule(License license) {
        this.license = license;
    }

    @Override
    public long nextScheduledTimeAfter(long startTime, long time) {
        long nextScheduledTime = -1;
        switch (getLicenseState(license, time)) {
            case ENABLED:
                nextScheduledTime = license.expiryDate();
                break;
            case GRACE_PERIOD:
                nextScheduledTime = license.expiryDate() + GRACE_PERIOD_DURATION.getMillis();
                break;
            case DISABLED:
                if (license.issueDate() > time) {
                    nextScheduledTime = license.issueDate();
                }
                break;
        }
        return nextScheduledTime;
    }
}