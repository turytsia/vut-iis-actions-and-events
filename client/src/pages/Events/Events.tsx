import React, { useContext, useEffect, useState } from 'react'

import classes from "./Events.module.css"
import { AppContext } from '../../context/AppContextProvider'
import PageView from '../../components/PageView/PageView'
import { EventType } from '../../utils/types'
import EventCard from './components/EventCard/EventCard'

const Events = () => {
  const context = useContext(AppContext)

  const [events, setEvents] = useState<EventType[]>([])

  const fetchEvents = async () => {
    try {
      const response = await context.request!.get("/events")

      const responses = await Promise.allSettled(
        response.data.events.map(async (id: number) => await context.request!.get(`/event/${id}`))
      );

      const fulfilledResponses = responses
        .filter((r): r is PromiseFulfilledResult<any> => r.status === "fulfilled")
        .map((r) => r.value)
        .filter((v) => v);

      setEvents(fulfilledResponses.map(({ data }) => data))

    } catch (error) {
      console.error(error)
    }
  }

  useEffect(() => {
    fetchEvents()
  }, [])

  return (
    <PageView scroll title='Events'>
      <div className={classes.events}>
        {events.map(event => <EventCard key={event.id} event={event} />)}
      </div>
    </PageView>
  )
}

export default Events