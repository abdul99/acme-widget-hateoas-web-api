package com.acme.widget.resource;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import com.acme.widget.domain.Widget;

public class WidgetResource extends Resource<Widget>
{
	String name;
	List<Link> whatsitLinks;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<Link> getWhatsitLinks()
	{
		return whatsitLinks;
	}

	public void setWhatsitLinks(List<Link> whatsitLinks)
	{
		this.whatsitLinks = whatsitLinks;
	}
}
