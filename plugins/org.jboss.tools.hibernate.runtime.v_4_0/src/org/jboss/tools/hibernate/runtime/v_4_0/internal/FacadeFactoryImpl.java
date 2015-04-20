package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.jboss.tools.hibernate.proxy.ClassMetadataProxy;
import org.jboss.tools.hibernate.proxy.CollectionMetadataProxy;
import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;

public class FacadeFactoryImpl extends AbstractFacadeFactory {
	
	public ClassLoader getClassLoader() {
		return FacadeFactoryImpl.class.getClassLoader();
	}
	
	@Override
	public IClassMetadata createClassMetadata(Object target) {
		return new ClassMetadataProxy(this, (ClassMetadata)target);
	}
	
	@Override
	public ICollectionMetadata createCollectionMetadata(Object target) {
		return new CollectionMetadataProxy(this, (CollectionMetadata)target);
	}
	
}
