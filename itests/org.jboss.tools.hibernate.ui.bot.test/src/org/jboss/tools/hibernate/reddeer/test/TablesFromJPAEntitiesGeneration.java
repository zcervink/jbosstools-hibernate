/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.reddeer.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.reddeer.common.exception.RedDeerException;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.db.DatabaseConfiguration;
import org.eclipse.reddeer.requirements.db.DatabaseRequirement;
import org.eclipse.reddeer.requirements.db.DatabaseRequirement.Database;
import org.eclipse.reddeer.workbench.impl.editor.TextEditor;
import org.jboss.tools.hibernate.reddeer.jpt.ui.wizard.GenerateDdlWizard;
import org.jboss.tools.hibernate.reddeer.jpt.ui.wizard.GenerateDdlWizardPage;
import org.jboss.tools.hibernate.ui.bot.test.ProjectUtils;
import org.jboss.tools.hibernate.ui.bot.test.factory.ConnectionProfileFactory;
import org.jboss.tools.hibernate.ui.bot.test.factory.DriverDefinitionFactory;
import org.jboss.tools.hibernate.ui.bot.test.factory.ProjectConfigurationFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;

/**
 * Generates ddl and tables from Entities
 * 
 * @author Jiri Peterka
 */
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
@Database
public class TablesFromJPAEntitiesGeneration extends HibernateRedDeerTest {
	
	@InjectRequirement
	private DatabaseRequirement dbRequirement;

	@Parameter
	public String prj;
	@Parameter(1)
	public String hbVersion;
	@Parameter(2)
	public String jpaVersion;
	@Parameter(3)
	public boolean useConsole;
	
	private final String DDL_FILE = "output.ddl";
	
	@Parameters(name="hibernate {1} use console: {3}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{"mvn-hibernate35-ent","3.5","2.0", true}, {"mvn-hibernate35-ent","3.5","2.0", false}, 
        		{"mvn-hibernate36-ent","3.6","2.0", true}, {"mvn-hibernate36-ent","3.6","2.0", false}, 
        		{"mvn-hibernate40-ent","4.0","2.0", true}, {"mvn-hibernate40-ent","4.0","2.0", false},
        		{"mvn-hibernate43-ent","4.3","2.1", true}, {"mvn-hibernate43-ent","4.3","2.1", false},
        		{"mvn-hibernate50-ent","5.0","2.1", true}, {"mvn-hibernate50-ent","5.0","2.1", false},
        		{"mvn-hibernate51-ent","5.1","2.1", true}, {"mvn-hibernate51-ent","5.1","2.1", false},
        		{"mvn-hibernate52-ent","5.2","2.1", true}, {"mvn-hibernate52-ent","5.2","2.1", false},
        		{"mvn-hibernate53-ent","5.3","2.1", true}, {"mvn-hibernate53-ent","5.3","2.1", false},//TODO change to JPA 2.2 when https://issues.jboss.org/browse/JBIDE-26566 is resolved
        		{"mvn-hibernate54-ent","5.4","2.1", true}, {"mvn-hibernate54-ent","5.4","2.1", false},//TODO change to JPA 2.2 when https://issues.jboss.org/browse/JBIDE-26566 is resolved
        		{"mvn-hibernate55-ent","5.5","2.1", true}, {"mvn-hibernate55-ent","5.5","2.1", false},//TODO change to JPA 2.2 when https://issues.jboss.org/browse/JBIDE-26566 is resolved
           });
    }
	
	
	@After
	public void cleanUp() {
		DatabaseConfiguration cfg = dbRequirement.getConfiguration();
		ConnectionProfileFactory.deleteConnectionProfile(cfg.getProfileName());
		deleteAllProjects();
	}
	
    @Test
    public void testDDLGeneration() {
    	testDDLGenerationMvn(useConsole);
    }
    
    private void testDDLGenerationMvn(boolean useConsole) {
    	prepareMavenProject();
    	testDDLGeneration(useConsole,hbVersion, "src/main/java");
    }
    
	private void testDDLGeneration(boolean useConsole, String hbVersion, String pkg ) {
		
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		pe.selectProjects(prj);
		GenerateDdlWizard w = new GenerateDdlWizard();
		w.open();
		GenerateDdlWizardPage p = new GenerateDdlWizardPage(w);
		p.setFileName(DDL_FILE);
		p.setUseConsoleConfiguration(useConsole);
		if(useConsole){
			assertTrue(p.isConsoleConfigurationEnabled());
			assertFalse(p.isHibernateVersionEnabled());
			p.setConsoleConfiguration(prj);
		}
		
		if (!useConsole) {
			assertFalse(p.isConsoleConfigurationEnabled());
			assertTrue(p.isHibernateVersionEnabled());
			p.setHibernateVersion(hbVersion);
		}
		w.finish();

		pe.open();
		try {
			ProjectUtils.getPackage(prj, DDL_FILE).open();
		} catch (RedDeerException e) {
			Assert.fail("DDL is not generated - known issues(s): JBIDE-19431, JBIDE-19535, JBIDE-26044");	
		}
		String ddlText = new TextEditor(DDL_FILE).getText();
		assertTrue("DDL file cannot be empty", ddlText.length() > 0);
		checkDDLContent(ddlText);
	}

	private void prepareMavenProject() {
		importMavenProject(prj);
		DatabaseConfiguration cfg = dbRequirement.getConfiguration();
		DriverDefinitionFactory.createDatabaseDriverDefinition(cfg);
		ConnectionProfileFactory.createConnectionProfile(cfg);
		ProjectConfigurationFactory.setProjectFacetForDB(prj, cfg, jpaVersion);
	}
	
	private void checkDDLContent(String text) {
		
		String[] expected = { 
		"create table SAKILA.PUBLIC.ADDRESS",
		"create table SAKILA.PUBLIC.CATEGORY",
		"create table SAKILA.PUBLIC.CITY",
		"create table SAKILA.PUBLIC.COUNTRY",
		"create table SAKILA.PUBLIC.CUSTOMER",
		"create table SAKILA.PUBLIC.FILM",
		"create table SAKILA.PUBLIC.FILM_ACTOR",
		"create table SAKILA.PUBLIC.FILM_CATEGORY",
		"create table SAKILA.PUBLIC.FILM_TEXT",
		"create table SAKILA.PUBLIC.INVENTORY",
		"create table SAKILA.PUBLIC.LANGUAGE",
		"create table SAKILA.PUBLIC.PAYMENT",
		"create table SAKILA.PUBLIC.RENTAL",
		"create table SAKILA.PUBLIC.STAFF",
		"create table SAKILA.PUBLIC.STORE",
		"alter table SAKILA.PUBLIC.ADDRESS",
		"alter table SAKILA.PUBLIC.CITY",
		"alter table SAKILA.PUBLIC.CUSTOMER",
		"alter table SAKILA.PUBLIC.FILM",
		"alter table SAKILA.PUBLIC.FILM_ACTOR",
		"alter table SAKILA.PUBLIC.FILM_CATEGORY",
		"alter table SAKILA.PUBLIC.INVENTORY",
		"alter table SAKILA.PUBLIC.INVENTORY",
		"alter table SAKILA.PUBLIC.PAYMENT",
		"alter table SAKILA.PUBLIC.RENTAL",
		"alter table SAKILA.PUBLIC.STAFF",
		"alter table SAKILA.PUBLIC.STORE"};
		
		for (String s : expected) {
			assertTrue(s + " is expected in ddl", text.contains(s));
		}
	}
}