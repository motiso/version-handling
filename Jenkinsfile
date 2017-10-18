


node {
        checkout scm
        util = load 'pipeline.groovy'
        def startedByWhat = util.jobStartedByWhat()
    
        if (startedByWhat == 'user') {
            echo 'Hello World from dev8'
        }
}
