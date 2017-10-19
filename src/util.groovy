

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
