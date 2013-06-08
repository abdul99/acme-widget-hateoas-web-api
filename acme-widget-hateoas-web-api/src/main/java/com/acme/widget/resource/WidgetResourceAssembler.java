package com.acme.widget.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.hateoas.mvc.IdentifiableResourceAssemblerSupport;

import com.acme.widget.domain.Whatsit;
import com.acme.widget.domain.Widget;
import com.acme.widget.web.controller.WhatsitController;
import com.acme.widget.web.controller.WidgetController;

public class WidgetResourceAssembler extends IdentifiableResourceAssemblerSupport<Widget, WidgetResource>
{
	private Logger log = LoggerFactory.getLogger(WidgetResourceAssembler.class);

	public WidgetResourceAssembler()
	{
		super(WidgetController.class, WidgetResource.class);
	}

	@Override
	public WidgetResource toResource(Widget entity)
	{
		// WidgetResource resource = new WidgetResource();
		WidgetResource resource = null;
		if (entity != null)
		{
			resource = createResourceWithId(entity, entity);
			resource.name = entity.getName();
			log.debug("converted entity {} to resource {}", entity, resource);
		}

		// embedded collection of links
		//
		List<Link> links = new ArrayList<Link>();
		Set<Whatsit> whatsits = entity.getWhatsits();
		for (Whatsit whatsit : whatsits)
		{
			Link link = ControllerLinkBuilder.linkTo(WhatsitController.class).slash(whatsit).withRel("whatsit")
					.withTitle(whatsit.getName());
			links.add(link);
		}
		resource.whatsitLinks = links;
		return resource;
	}
}