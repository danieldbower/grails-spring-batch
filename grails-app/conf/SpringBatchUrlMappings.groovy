class SpringBatchUrlMappings {
    static mappings = {
        name batch: "/batch/$controller/$action?/$id?(.$format)?"(plugin: 'springBatch')
    }
}



