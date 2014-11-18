log4j = {
    error  'org.codehaus.groovy.grails',
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}
grails.views.default.codec="html" // none, html, base64
grails.views.gsp.encoding="UTF-8"

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside null
                scriptlet = 'none' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
