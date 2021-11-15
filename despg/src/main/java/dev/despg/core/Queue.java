/**
 * Copyright (C) 2021 despg.dev, Ralf Buschermöhle
 *
 * DESPG is made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * see LICENSE
 *
 */
package dev.despg.core;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Queue<T extends SimulationObject> extends SimulationObject
{
	private String name;
	private List<T> elements;
	private List<T> delayedElements;

	private static EventQueue eventQueue = EventQueue.getInstance();

	public Queue(String name)
	{
		this.name = name;
		elements = new LinkedList<>();
		delayedElements = new LinkedList<>();
		SimulationObjects.getInstance().add(this);
	}

	public T add(T element)
	{
		elements.add(element);
		return element;
	}

	public void addWithDelay(T element, int delay, int timeStep)
	{
		delayedElements.add(element);
		eventQueue.add(new Event(timeStep + delay, CoreEventTypes.Delay, element, this.getClass(), this));
	}

	public T getNext()
	{
		if (elements.isEmpty())
			return null;

		return elements.get(0);
	}

	public T getNext(Map<String, Object> filters)
	{
		if (elements.isEmpty())
			return null;

		T matchingElement = filters == null || filters.isEmpty() ? elements.get(0) : null;

		for (int indexQueuedElement = 0; indexQueuedElement < elements.size()
				&& matchingElement == null; indexQueuedElement++)
		{
			T queuedElement = elements.get(indexQueuedElement);
			boolean allFilterTrue = true;

			for (Entry<String, Object> filterEntry : filters.entrySet())
			{
				String attribute = filterEntry.getKey();
				Object filter = filterEntry.getValue();
				String getter = "get"
						+ (attribute.length() > 1 ? attribute.toUpperCase().charAt(0) + attribute.substring(1)
								: attribute.toUpperCase());
				try
				{
					Method method = queuedElement.getClass().getMethod(getter);
					Object attributeValue = method.invoke(queuedElement);

					if (filter instanceof Filter)
					{
						Filter filterExpression = (Filter) filter;
						if (!filterExpression.filter(attributeValue))
							allFilterTrue = false;
					}
					else if (filter instanceof Number || filter instanceof Boolean)
					{
						if (filter instanceof Number && attributeValue == null && ((Number) filter).doubleValue() == 0)
							continue;
						else if (!attributeValue.equals(filter))
							allFilterTrue = false;
					}
					else if (attributeValue != filter)
						allFilterTrue = false;
				}
				catch (Exception e)
				{
					allFilterTrue = false;
				}
			}

			if (allFilterTrue)
				matchingElement = queuedElement;
		}

		return matchingElement;
	}

	public T remove(T element)
	{
		if (elements.remove(element))
			return element;
		return null;
	}

	@Override
	public boolean simulate(int timeStep)
	{
		Event event = eventQueue.getNextEvent(timeStep, true, CoreEventTypes.Delay, this.getClass(), this);

		if (event != null && delayedElements.remove(event.getObjectAttached()))
		{
			elements.add((T) event.getObjectAttached());
			eventQueue.remove(event);
			return true;
		}

		return false;
	}

	@Override
	public String toString()
	{
		return name + " elements:" + elements + " delayedElements:" + delayedElements;
	}

}
