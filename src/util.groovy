 
@Grapes(
    @Grab(group='org.xmlunit', module='xmlunit-matchers', version='2.4.0')
)

import groovy.xml.StreamingMarkupBuilder
import groovy.util.XmlNodePrinter
import groovy.xml.XmlUtil
import org.xmlunit.diff.*
import org.xmlunit.*
import org.xmlunit.builder.DiffBuilder
import org.xmlunit.builder.Input

@NonCPS
def pomFilesAreIdentical(pomPrevBranchDir,pomCurrentBranchDir)
{
    	def pomPrevBranch = getNewPomForRelease(pomPrevBranchDir,1)
	def pomCurrentBranch = getNewPomForRelease(pomCurrentBranchDir,1)
	println "checking pom differences"
	
	def myDiff = DiffBuilder.compare(Input.fromString(pomPrevBranch.toString()))
		    .withTest(Input.fromString(pomCurrentBranch.toString()))
		    .checkForSimilar()
		    .withNodeMatcher(new DefaultNodeMatcher(new ByNameAndTextRecSelector(),ElementSelectors.byName))
		    .build()

	
	println myDiff.identical()
	println myDiff.hasDifferences()
	return myDiff.identical()
	

}  

def writeNewPom(pomDir,versionNumer)
{ 
    def result = getNewPomForRelease(pomDir,versionNumer)
    def dir = new File(pomDir, 'pom.xml')
	def writer = dir.newWriter()
	writer << XmlUtil.serialize(result)
	writer.close()
  return writer
}


@NonCPS
def getNewPomForRelease(pomDir,versionNumer)
{
    println "Release Script Start -----"
    
    def dir = new File(pomDir, 'pom.xml')
    
    def pom = new XmlSlurper( false, false ).parse(dir)
    
    // increase version number
    def version = pom.version.toString().replace("-SNAPSHOT", "").split("\\.")
    //version[-1] = version[-1].toInteger()+1
    println "Previoues version of pom: ${pom.version}"
    pom.version = versionNumer 
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
    println "creating new pom.xml "
    def outputBuilder = new StreamingMarkupBuilder()
    def result = outputBuilder.bind{ 
        mkp.yield pom 
        //mkp.declareNamespace("":  "http://maven.apache.org/POM/4.0.0")
    }
     
    return result
    
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
