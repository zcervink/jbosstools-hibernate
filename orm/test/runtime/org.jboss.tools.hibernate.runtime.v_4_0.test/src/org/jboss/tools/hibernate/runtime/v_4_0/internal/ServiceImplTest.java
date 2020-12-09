package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IDatabaseReader;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.junit.Assert;
import org.junit.Test;

public class ServiceImplTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private ServiceImpl service = new ServiceImpl();

	@Test
	public void testNewAnnotationConfiguration() {
		IConfiguration configuration = service.newAnnotationConfiguration();
		Assert.assertNotNull(configuration);
		Object target = ((IFacade)configuration).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof Configuration);
	}
	
	@Test
	public void testNewDatabaseReader() {
		IConfiguration configuration = 
				FACADE_FACTORY.createConfiguration(
						new Configuration());
		IReverseEngineeringStrategy engineeringStrategy = 
				FACADE_FACTORY.createReverseEngineeringStrategy(
						new DefaultReverseEngineeringStrategy());
		IDatabaseReader databaseReaderFacade = service.newDatabaseReader(
				configuration, 
				engineeringStrategy);
		Assert.assertNotNull(databaseReaderFacade);
		JDBCReader reader = (JDBCReader)((IFacade)databaseReaderFacade).getTarget();
		Assert.assertNotNull(reader);		
	}
	
	@Test
	public void testNewDialect() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:");
		String dialect = service.newDialect(new Properties(), connection);
		Assert.assertEquals("org.hibernate.dialect.H2Dialect", dialect);
	}

	@Test
	public void testNewTable() {
		ITable table = service.newTable("foo");
		Assert.assertNotNull(table);
		Object target = ((IFacade)table).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof Table);
		Assert.assertEquals("foo", ((Table)target).getName());
		Assert.assertNotNull(((Table)target).getPrimaryKey());
	}
	
}
