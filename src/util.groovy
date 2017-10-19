
@NonCPS
def jobStartedByWhat() {
def startedByWhat = ''
try {
    def buildCauses = currentBuild.rawBuild.getCauses()
    for ( buildCause in buildCauses ) {
        if (buildCause != null) {
            def causeDescription = buildCause.getShortDescription()
            echo "shortDescription: ${causeDescription}"
            if (causeDescription.contains("Started by timer")) {
                startedByWhat = 'timer'
            }
            if (causeDescription.contains("Started by user")) {
                startedByWhat = 'user'
            }
        }
    }
} catch(theError) {
    echo "Error getting build cause: ${theError}"
}

return startedByWhat
}

return this;
