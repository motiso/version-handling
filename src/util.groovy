 import groovy.xml.StreamingMarkupBuilder
 import groovy.util.XmlNodePrinter
 import org.codehaus.groovy.tools.xml.DomToGroovy
 import groovy.xml.XmlUtil
 import groovy.util.AntBuilder

@Grapes(
    @Grab(group='org.xmlunit', module='xmlunit-matchers', version='2.4.0')
)

import groovy.xml.StreamingMarkupBuilder
import groovy.util.XmlNodePrinter
import org.codehaus.groovy.tools.xml.DomToGroovy
import groovy.xml.XmlUtil
import groovy.util.AntBuilder
import org.custommonkey.xmlunit.*
import org.xmlunit.diff.*
import org.xmlunit.*
import org.xmlunit.builder.DiffBuilder
import org.xmlunit.builder.Input


def comparePomFiles(pomPrevBranch,pomCurrentBranch)
{
    
def myDiff = DiffBuilder.compare(Input.fromString(pomPrevBranch))
            .withTest(Input.fromString(pomCurrentBranch))
            .checkForSimilar()
            .withNodeMatcher(new DefaultNodeMatcher(new ByNameAndTextRecSelector(),ElementSelectors.byName))
            .build()
println myDiff.toString()
println myDiff.hasDifferences()

}  

def writeNewPom(result,pomDir)
{ 
    def dir = new File(pomDir, 'pom.xml')
	def writer = dir.newWriter()
	writer << XmlUtil.serialize(result)
	writer.close()
  return writer
}

def removePomDependencies(pomDir)
{
    AntBuilder ant = new AntBuilder();
    
    println "Release Script Start -----"
    
    def dir = new File(pomDir, 'pom.xml')
    // copy a backup of the pom
    println "A copy of the old pom is saved in pom.backup.xml"
    ant.copy(file: dir, tofile: "pom.backup.xml")
    
    def pom = new XmlSlurper( false, false ).parse(dir)
    
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
        mkp.yield pom 
        //mkp.declareNamespace("":  "http://maven.apache.org/POM/4.0.0")
    }
     
    def writer = new StringWriter()
	writer << XmlUtil.serialize(result)
	
    def retVal = writer.toString()
  	println retVal
  	writer.close()
    return retVal
    
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
