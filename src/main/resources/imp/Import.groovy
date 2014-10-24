def readChildren (parent, children) {
    children.each {
        if (it.name() == 'children') {
            // special XML tag
            readChildren (parent, it.children())
        }
        else {
            def type = types.find {t -> t.javaType.simpleName == it.name()}
            if (type == null)
                type = embeddables.find {t -> t.javaType.simpleName == it.name()}

            def p = type.getJavaType().newInstance()
            it.attributes().each { k, v ->
//                if (p.metaClass.hasProperty(k)) {
                    p."$k" = v
                    println ("attribute: $k = " + p."$k")
//                }
            }

            if (parent != null) {
                def childrenField = it.parent.@name.text()
                def parentField = it.parent.@parent.text()
                
                println (' : ' + childrenField.class + ' - ' + childrenField + '/ ' + it.parent.name())

                if (childrenField != null && childrenField != '') {
                    def addChildren = 'add' + childrenField.capitalize()
                    println ('try to set attribute: ' + addChildren)
                    
//                    if (parent.metaClass.hasProperty(addChildren)) {
                        parent."$addChildren"(p)
                        println ('OK: ' + addChildren)
//                    }
//                    else if (parent.metaClass.hasProperty(childrenField)) {
//                        parent."$childrenField".add (p)
//                        println ('OK: add to ' + $childrenField)
//                    }
                }
                
                if (parentField != null && parentField != '') {
                    p."$parentField" = parent
                }
            }

            objectList.add (p)
            readChildren (p, it.children())
        }
    }
}

objectList = []
def xmlData = xmlSource
println ('Begin load XML...')
def dataNode = new XmlSlurper().parseText(xmlData)
readChildren (null, dataNode.children())
println ('XML Loaded')
objectList