@Library('util')

def myUtils = new util()

node {
        checkout scm
        def projectBase = "${workspace}"
        myUtils.fixPoms(projectBase)
}
