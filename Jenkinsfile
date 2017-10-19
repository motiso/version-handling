@Library('util')

def myUtils = new util()

node {
        checkout scm
        myUtils.fixPoms(project.basedir)
}
