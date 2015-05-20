package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.jboss.tools.hibernate.runtime.common.AbstractHQLCompletionProposalFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class HQLCompletionProposalFacadeImpl 
extends AbstractHQLCompletionProposalFacade {
	
	public HQLCompletionProposalFacadeImpl(
			IFacadeFactory facadeFactory,
			HQLCompletionProposal proposal) {
		super(facadeFactory, proposal);
	}

	public HQLCompletionProposal getTarget() {
		return (HQLCompletionProposal)super.getTarget();
	}

	@Override
	public int propertyKind() {
		return HQLCompletionProposal.PROPERTY;
	}

	@Override
	public int keywordKind() {
		return HQLCompletionProposal.KEYWORD;
	}

	@Override
	public int functionKind() {
		return HQLCompletionProposal.FUNCTION;
	}

}
