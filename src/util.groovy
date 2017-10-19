def fixPoms()
{
        import groovy.xml.StreamingMarkupBuilder
        import groovy.util.XmlNodePrinter
        import org.codehaus.groovy.tools.xml.DomToGroovy
        import groovy.xml.XmlUtil

        println "Release Script Start -----"

        def dir = new File(project.basedir, 'pom.xml')
        // copy a backup of the pom
        println "A copy of the old pom is saved in pom.backup.xml"
        ant.copy(file: dir, tofile: "pom.backup.xml")

        def pom = new XmlSlurper().parse(dir)

        // increase version number
        def version = pom.version.toString().replace("-SNAPSHOT", "").split("\\.")
        version[-1] = version[-1].toInteger()+1
        println "Previoues version of pom: ${pom.version}"
        pom.version = version.join('.') 
        println "New Version of pom: ${pom.version}"


        // remove snapshots in properties
        print "remove snapshots in properties "
        pom.properties.childNodes().each{
            it.replaceBody(it.text().replace("-SNAPSHOT", ""))		
        }
        println "OK"

        // remove snapshots in dependencies
        print "remove snapshots in dependencies "
        pom.dependencies.dependency.each{
            it.version.replaceBody(it.version.text().replace("-SNAPSHOT", ""))		
        }
        println "OK"



        // output the pom
        print "Writing new pom.xml "
        def outputBuilder = new StreamingMarkupBuilder()
        String result = outputBuilder.bind{ 
            mkp.declareNamespace("":  "http://maven.apache.org/POM/4.0.0")
            mkp.yield pom 
        }
        def writer = dir.newWriter()
        writer << XmlUtil.serialize(result)
        writer.close()
        println "OK"
        println "Release Script End -----"
}
def jobStartedCause() {
        def startedCause = ''
        try {
            def buildCauses = currentBuild.rawBuild.getCauses()
            for ( buildCause in buildCauses ) {
                if (buildCause != null) {
                    def causeDescription = buildCause.getShortDescription()
                    echo "shortDescription: ${causeDescription}"
                    if (causeDescription.contains("Started by timer")) {
                        startedCause = 'timer'
                    }
                    if (causeDescription.contains("Started by user")) {
                        startedCause = 'user'
                    }
                }
            }
        } catch(theError) {
            echo "Error getting build cause: ${theError}"
        }

return startedCause
}

return this
