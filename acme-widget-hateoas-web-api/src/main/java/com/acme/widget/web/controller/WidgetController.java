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

import com.acme.widget.dao.jpa.WidgetDao;
import com.acme.widget.domain.Widget;
import com.acme.widget.resource.WidgetResource;
import com.acme.widget.resource.WidgetResourceAssembler;
import com.kerz.beans.PropertyCopier;

@Controller
@RequestMapping("/widgets")
public class WidgetController implements InitializingBean
{
	private static final Logger log = LoggerFactory.getLogger(WidgetController.class);
	// public static final String WIDGETS = "/widgets";
	private static final String WIDGET_ID = "widgetId";
	public static final String WIDGET = "/{" + WIDGET_ID + "}";

	@Autowired
	private WidgetDao widgetDao;

	@Autowired
	PropertyCopier propertyCopier;

	WidgetResourceAssembler resourceAssembler = new WidgetResourceAssembler();

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	List<WidgetResource> index()
	{
		List<Widget> entities = widgetDao.findAll(new Sort("name"));
		log.debug("entities={}", entities);
		return resourceAssembler.toResources(entities);
	}

	@RequestMapping(value = WIDGET, method = RequestMethod.GET)
	public ResponseEntity<WidgetResource> get(@PathVariable Long id)
	{
		Widget entity = widgetDao.findOne(id);
		log.debug("id={}, entity={}", id, entity);
		return new ResponseEntity<WidgetResource>(resourceAssembler.toResource(entity), entity != null ? HttpStatus.OK
				: HttpStatus.NOT_FOUND);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> post(@Valid @RequestBody Widget entity, Errors errors) throws URISyntaxException
	// public ResponseEntity<?> post(@Valid @RequestBody Widget widget) throws
	// URISyntaxException
	{
		log.debug("widget={}, errors={}", entity, errors);

		if (errors.hasErrors())
		{
			return new ResponseEntity<Errors>(errors, HttpStatus.BAD_REQUEST);
		}

		// current setup allows post json to set 'version' prop. if this is an
		// issue, can use propertyCopier configured to filter version prop.

		widgetDao.save(entity);
		HttpHeaders headers = new HttpHeaders();
		// headers.setLocation(new URI(WIDGETS + '/' + entity.getId()));
		headers.setLocation(ControllerLinkBuilder.linkTo(getClass()).slash(entity).toUri());
		return new ResponseEntity<WidgetResource>(resourceAssembler.toResource(entity), headers, HttpStatus.OK);
	}

	@RequestMapping(value = WIDGET, method = RequestMethod.PUT)
	public ResponseEntity<?> put(@PathVariable Long id, @Valid @RequestBody Widget entity, Errors errors)
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

		Widget putEntity = widgetDao.findOne(id);

		HttpStatus status = null;
		if (putEntity == null)
		{
			status = HttpStatus.NOT_FOUND;
		}
		else
		{
			propertyCopier.copyProperties(entity, putEntity);
			putEntity = widgetDao.save(putEntity);
			status = HttpStatus.OK;
		}
		HttpHeaders headers = new HttpHeaders();
		// headers.setLocation(new URI(WIDGETS + '/' + widgetId));
		headers.setLocation(ControllerLinkBuilder.linkTo(getClass()).slash(entity).toUri());
		return new ResponseEntity<Widget>(putEntity, headers, status);
	}

	@RequestMapping(value = WIDGET, method = RequestMethod.DELETE)
	public ResponseEntity<HttpStatus> delete(@PathVariable Long id) throws URISyntaxException
	{
		log.debug("id={}", id);

		Widget widget = widgetDao.findOne(id);

		HttpStatus status = null;
		if (widget == null)
		{
			status = HttpStatus.NOT_FOUND;
		}
		else
		{
			widgetDao.delete(id);
			status = HttpStatus.OK;
		}

		return new ResponseEntity<HttpStatus>(status);
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		Assert.notNull(widgetDao, "widget-dao required");
		Assert.notNull(propertyCopier, "property-copier required");
	}
}
