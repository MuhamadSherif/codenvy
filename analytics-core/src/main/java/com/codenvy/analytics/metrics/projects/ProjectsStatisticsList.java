/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.analytics.metrics.projects;

import com.codenvy.analytics.metrics.AbstractListValueResulted;
import com.codenvy.analytics.metrics.Context;
import com.codenvy.analytics.metrics.MetricType;
import com.codenvy.analytics.metrics.ReadBasedSummariziable;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import javax.annotation.security.RolesAllowed;

/**
 * @author Alexander Reshetnyak
 */
@RolesAllowed(value = {"system/admin", "system/manager"})
public class ProjectsStatisticsList extends AbstractListValueResulted implements ReadBasedSummariziable {


    public ProjectsStatisticsList() {
        super(MetricType.PROJECTS_STATISTICS_LIST);
    }

    @Override
    public String getDescription() {
        return "Users' projects statistics data";
    }

    @Override
    public String getStorageCollectionName() {
        return getStorageCollectionName(MetricType.PROJECTS_STATISTICS);
    }

    @Override
    public String[] getTrackedFields() {
        return new String[]{PROJECT,
                            WS,
                            CODE_REFACTORIES,
                            CODE_COMPLETES,
                            BUILDS,
                            RUNS,
                            DEBUGS,
                            DEPLOYS,
                            BUILD_INTERRUPTS,
                            ARTIFACT_DEPLOYS,
                            PROJECT_CREATES,
                            PROJECT_DESTROYS,
                            PAAS_DEPLOYS,
                            RUN_TIME,
                            BUILD_TIME,
                            DEBUG_TIME,
                            PROJECT_TYPE,
                            DATE,
                            USER
        };
    }

    @Override
    public DBObject[] getSpecificDBOperations(Context clauses) {
        DBObject group = new BasicDBObject();
    
        group.put(ID, new BasicDBObject("PROJECT_ID", "$" + PROJECT_ID));
        group.put(CODE_REFACTORIES, new BasicDBObject("$sum", "$" + CODE_REFACTORIES));
        group.put(CODE_COMPLETES, new BasicDBObject("$sum", "$" + CODE_COMPLETES));
        group.put(BUILDS, new BasicDBObject("$sum", "$" + BUILDS));
        group.put(RUNS, new BasicDBObject("$sum", "$" + RUNS));
        group.put(DEBUGS, new BasicDBObject("$sum", "$" + DEBUGS));
        group.put(DEPLOYS, new BasicDBObject("$sum", "$" + DEPLOYS));
        group.put(FACTORIES, new BasicDBObject("$sum", "$" + FACTORIES));
        group.put(BUILD_INTERRUPTS, new BasicDBObject("$sum", "$" + BUILD_INTERRUPTS));
        group.put(ARTIFACT_DEPLOYS, new BasicDBObject("$sum", "$" + ARTIFACT_DEPLOYS));
        group.put(PROJECT_CREATES, new BasicDBObject("$sum", "$" + PROJECT_CREATES));
        group.put(PROJECT_DESTROYS, new BasicDBObject("$sum", "$" + PROJECT_DESTROYS));
        group.put(PAAS_DEPLOYS, new BasicDBObject("$sum", "$" + PAAS_DEPLOYS));
        group.put(RUN_TIME, new BasicDBObject("$sum", "$" + RUN_TIME));
        group.put(BUILD_TIME, new BasicDBObject("$sum", "$" + BUILD_TIME));
        group.put(DEBUG_TIME, new BasicDBObject("$sum", "$" + DEBUG_TIME));
        group.put(PROJECT, new BasicDBObject("$first", "$" + PROJECT));
        group.put(WS, new BasicDBObject("$first", "$" + WS));
        group.put(USER, new BasicDBObject("$first", "$" + USER));
        group.put(DATE, new BasicDBObject("$first", "$" + DATE));
        group.put(PROJECT_TYPE, new BasicDBObject("$first", "$" + PROJECT_TYPE));
    
        DBObject project = new BasicDBObject();
        project.put(CODE_REFACTORIES, "$" + CODE_REFACTORIES);
        project.put(CODE_COMPLETES, "$" + CODE_COMPLETES);
        project.put(BUILDS, "$" + BUILDS);
        project.put(RUNS, "$" + RUNS);
        project.put(DEBUGS, "$" + DEBUGS);
        project.put(DEPLOYS, "$" + DEPLOYS);
        project.put(FACTORIES, "$" + FACTORIES);
        project.put(BUILD_INTERRUPTS, "$" + BUILD_INTERRUPTS);
        project.put(ARTIFACT_DEPLOYS, "$" + ARTIFACT_DEPLOYS);
        project.put(PROJECT_CREATES, "$" + PROJECT_CREATES);
        project.put(PROJECT_DESTROYS, "$" + PROJECT_DESTROYS);
        project.put(PAAS_DEPLOYS, "$" + PAAS_DEPLOYS);
        project.put(RUN_TIME, "$" + RUN_TIME);
        project.put(BUILD_TIME, "$" + BUILD_TIME);
        project.put(DEBUG_TIME, "$" + DEBUG_TIME);
        project.put(PROJECT, "$" + PROJECT);
        project.put(WS, "$" + WS);
        project.put(USER, "$" + USER);
        project.put(DATE, "$" + DATE);
        project.put(PROJECT_TYPE, "$" + PROJECT_TYPE);
    
    
        return new DBObject[]{new BasicDBObject("$group", group),
                              new BasicDBObject("$project", project)};
    }

    @Override
    public DBObject[] getSpecificSummarizedDBOperations(Context clauses) {
        DBObject[] dbOperations = getSpecificDBOperations(clauses);
        ((DBObject)(dbOperations[0].get("$group"))).put(ID, null);
        ((DBObject)(dbOperations[1].get("$project"))).removeField(WS);
        ((DBObject)(dbOperations[1].get("$project"))).removeField(PROJECT);
        ((DBObject)(dbOperations[1].get("$project"))).removeField(PROJECT_TYPE);
        ((DBObject)(dbOperations[1].get("$project"))).removeField(USER);
        ((DBObject)(dbOperations[1].get("$project"))).removeField(DATE);

        return dbOperations;
    }
}
