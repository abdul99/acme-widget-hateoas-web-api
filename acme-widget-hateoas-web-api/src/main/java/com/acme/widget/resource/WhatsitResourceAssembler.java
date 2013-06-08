package com.acme.widget.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.mvc.IdentifiableResourceAssemblerSupport;

import com.acme.widget.domain.Whatsit;
import com.acme.widget.web.controller.WhatsitController;

public class WhatsitResourceAssembler extends IdentifiableResourceAssemblerSupport<Whatsit, WhatsitResource>
{
	private Logger log = LoggerFactory.getLogger(WhatsitResourceAssembler.class);

	public WhatsitResourceAssembler()
	{
		super(WhatsitController.class, WhatsitResource.class);
	}

	@Override
	public WhatsitResource toResource(Whatsit entity)
	{
		WhatsitResource resource = null;
		if (entity != null)
		{
			resource = createResourceWithId(entity, entity);
			resource.name = entity.getName();
			log.debug("converted entity {} to resource {}", entity, resource);
		}
		// whatsitLinks!
		// Link link =
		// resource.add();
		return resource;
	}
}