package com.acme.widget.resource;

import org.springframework.hateoas.Resource;

import com.acme.widget.domain.Whatsit;

public class WhatsitResource extends Resource<Whatsit>
{
	String name;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
