package com.acme.widget.web.controller;

import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.acme.widget.dao.jpa.WhatsitDao;
import com.acme.widget.domain.Whatsit;
import com.acme.widget.resource.WhatsitResource;
import com.acme.widget.resource.WhatsitResourceAssembler;
import com.kerz.beans.PropertyCopier;

@Controller
@RequestMapping("/whatsits")
public class WhatsitController implements InitializingBean
{
	private static final Logger log = LoggerFactory.getLogger(WhatsitController.class);
	private static final String ID = "id";
	public static final String ENTITY = "/{" + ID + "}";

	@Autowired
	private WhatsitDao dao;

	@Autowired
	PropertyCopier propertyCopier;

	WhatsitResourceAssembler resourceAssembler = new WhatsitResourceAssembler();

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	List<WhatsitResource> index()
	{
		List<Whatsit> entities = dao.findAll(new Sort("name"));
		log.debug("entities={}", entities);
		return resourceAssembler.toResources(entities);
	}

	@RequestMapping(value = ENTITY, method = RequestMethod.GET)
	public ResponseEntity<WhatsitResource> get(@PathVariable Long id)
	{
		Whatsit entity = dao.findOne(id);
		log.debug("id={}, entity={}", id, entity);
		return new ResponseEntity<WhatsitResource>(resourceAssembler.toResource(entity), entity != null ? HttpStatus.OK
				: HttpStatus.NOT_FOUND);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> post(@Valid @RequestBody Whatsit entity, Errors errors) throws URISyntaxException
	// public ResponseEntity<?> post(@Valid @RequestBody Widget widget) throws
	// URISyntaxException
	{
		log.debug("entity={}, errors={}", entity, errors);

		if (errors.hasErrors())
		{
			return new ResponseEntity<Errors>(errors, HttpStatus.BAD_REQUEST);
		}

		// current setup allows post json to set 'version' prop. if this is an
		// issue, can use propertyCopier configured to filter version prop.

		dao.save(entity);
		HttpHeaders headers = new HttpHeaders();
		// headers.setLocation(new URI(WIDGETS + '/' + entity.getId()));
		headers.setLocation(ControllerLinkBuilder.linkTo(getClass()).slash(entity).toUri());
		return new ResponseEntity<WhatsitResource>(resourceAssembler.toResource(entity), headers, HttpStatus.OK);
	}

	@RequestMapping(value = ENTITY, method = RequestMethod.PUT)
	public ResponseEntity<?> put(@PathVariable Long id, @Valid @RequestBody Whatsit entity, Errors errors)
			throws URISyntaxException
	{
		log.debug("id={}, entity={}, errors={}", id, entity, errors);

		if (entity.getVersion() == null)
		{
			errors.rejectValue("version", "field-required");
		}

		if (errors.hasErrors())
		{
			return new ResponseEntity<Errors>(errors, HttpStatus.BAD_REQUEST);
		}

		Whatsit putEntity = dao.findOne(id);

		HttpStatus status = null;
		if (putEntity == null)
		{
			status = HttpStatus.NOT_FOUND;
		}
		else
		{
			propertyCopier.copyProperties(entity, putEntity);
			putEntity = dao.save(putEntity);
			status = HttpStatus.OK;
		}
		HttpHeaders headers = new HttpHeaders();
		// headers.setLocation(new URI(WIDGETS + '/' + widgetId));
		headers.setLocation(ControllerLinkBuilder.linkTo(getClass()).slash(entity).toUri());
		return new ResponseEntity<Whatsit>(putEntity, headers, status);
	}

	@RequestMapping(value = ENTITY, method = RequestMethod.DELETE)
	public ResponseEntity<HttpStatus> delete(@PathVariable Long id) throws URISyntaxException
	{
		log.debug("id={}", id);

		Whatsit entity = dao.findOne(id);

		HttpStatus status = null;
		if (entity == null)
		{
			status = HttpStatus.NOT_FOUND;
		}
		else
		{
			dao.delete(id);
			status = HttpStatus.OK;
		}

		return new ResponseEntity<HttpStatus>(status);
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		Assert.notNull(dao, "dao required");
		Assert.notNull(propertyCopier, "property-copier required");
	}
}
