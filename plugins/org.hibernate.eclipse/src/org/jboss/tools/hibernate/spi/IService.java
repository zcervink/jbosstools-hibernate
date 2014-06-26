package org.jboss.tools.hibernate.spi;

import java.io.File;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.ide.completion.HQLCodeAssist;

public interface IService {

	IConfiguration newAnnotationConfiguration();

	IConfiguration newJpaConfiguration(
			String entityResolver,
			String persistenceUnit, 
			Map<Object, Object> overrides);
	
	IConfiguration newDefaultConfiguration();
	
	void setExporterConfiguration(
			IExporter exporter, 
			IConfiguration hcfg);
	
	HibernateMappingExporter newHibernateMappingExporter(
			IConfiguration hcfg, 
			File file);
	
	ISchemaExport newSchemaExport(
			IConfiguration hcfg);
	
	HQLCodeAssist newHQLCodeAssist(
			IConfiguration hcfg);

	IConfiguration newJDBCMetaDataConfiguration();
	
	IExporter createExporter(
			String exporterClassName);
	
	IArtifactCollector newArtifactCollector();
	
	IHQLQueryPlan newHQLQueryPlan(
			String query, 
			boolean shallow, 
			ISessionFactory sessionFactory);
	
	ITypeFactory newTypeFactory();
	
	INamingStrategy newNamingStrategy(String strategyClassName);
	
	IOverrideRepository newOverrideRepository();

	ITableFilter newTableFilter();

	IReverseEngineeringSettings newReverseEngineeringSettings(
			IReverseEngineeringStrategy res);

	IReverseEngineeringStrategy newDefaultReverseEngineeringStrategy();

	IJDBCReader newJDBCReader(Properties properties, ISettings settings,
			IReverseEngineeringStrategy strategy);

	IReverseEngineeringStrategy newReverseEngineeringStrategy(
			String strategyName, 
			IReverseEngineeringStrategy delegate);

	String getReverseEngineeringStrategyClassName();

	IDatabaseCollector newDatabaseCollector(IMetaDataDialect metaDataDialect);

	IProgressListener newProgressListener(IProgressMonitor monitor);
	
	ICfg2HbmTool newCfg2HbmTool();
	
	IProperty newProperty();
	
	ITable newTable(String name);

	IColumn newColumn(String string);
	
	IDialect newDialect(Properties properties, Connection connection);
	
	Class<?> getDriverManagerConnectionProviderClass();

	IEnvironment getEnvironment();
	
}
