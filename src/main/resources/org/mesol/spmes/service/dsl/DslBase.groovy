package org.mesol.spmes.service.dsl

abstract class DslBase extends Script implements GroovyInterceptable {
    def beans = [:]

    def findBean = {name ->
        if (beans[name]) {
            beans[name]
        }
        else {
            try {
                beans[name] = context.getBean (name)
            }
            catch (e) {
                //e.printStackTrace(System.err)
            }
        }
    }

    def findEntity = {name ->
        def found = entities.stream().filter {w -> name == w.name}
        def opt = found.findFirst()
        if (opt.present) {
            opt.get()
        }
        else {
            found = embeddables.stream().filter {w -> name == w.javaType.simpleName}
            opt = found.findFirst()
            if (opt.present) {
                opt.get()
            }
        }
    }

    def invokeMethod(String name, args) {
        def bean = findBean(name)
        if (!bean) {
            System.err.println ('Bean not found: ' + name)
            def entity = findEntity (name)
            entity = entity?.javaType?.newInstance()
            args[0].each {key, value ->
                entity?."${key}" = value
            }
            entity
        }
        else {
            // change delegate for function call
            args[0].delegate = bean
            // change strategy
            args[0].resolveStrategy = Closure.DELEGATE_FIRST
            if (args.length > 1) {
                def newargs = []
                for (i in 1..args.lenth) {
                    newargs.add (args[i])
                }

                System.out.println ('newargs: ' + newargs)
                args[0](newargs)
            }
            else
                args[0]()
        }
    }
}
