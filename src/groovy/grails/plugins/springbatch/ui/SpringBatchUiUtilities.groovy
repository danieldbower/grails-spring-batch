package grails.plugins.springbatch.ui

import groovy.util.logging.Log4j

@Log4j
class SpringBatchUiUtilities {

    static Long getDuration(Date start, Date end) {
        if(!start) {
            log.error("Must provide start")
            return null
        }
        return end?.time ? end.time - start.time : new Date().time - start.time
    }

    static List paginate(int offset, int max, Closure c) {
        def list = c.call() as List
        return paginateInternal(list, max, offset)
    }

    private static List paginateInternal(List list, int max, int offset=0 ) {
        ((max as Integer) <= 0 || (offset as Integer) < 0) ? [] : list.subList( Math.min( offset as Integer, list.size() ), Math.min( (offset as Integer) + (max as Integer), list.size() ) )
    }
}
