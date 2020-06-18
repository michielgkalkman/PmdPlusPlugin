package org.taHjaj.wo.pmdplus.dup;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

@Mojo( name = "dup", defaultPhase = LifecyclePhase.TEST, requiresDependencyResolution = ResolutionScope.TEST)
public class DupMojo extends AbstractMojo {
    /**
     * The project currently being build.
     */
    @Parameter( defaultValue = "${project}", readonly = true )
    private MavenProject mavenProject;

    /**
     * The current Maven session.
     */
    @Parameter( defaultValue = "${session}", readonly = true )
    private MavenSession mavenSession;

    /**
     * The Maven BuildPluginManager component.
     */
    @Component
    private BuildPluginManager pluginManager;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("Executing with maven project " + mavenProject + " for session " + mavenSession);

        // Find the license plugin (it's project's responsibility to make sure the License plugin is properly setup in
        // its <pluginManagement>, for most XWiki projects it just mean inherits from xwiki-commons-pom)
        Plugin plugin = plugin( "org.apache.maven.plugins","maven-pmd-plugin", "3.13.0", dependencies(
                dependency( "org.taHjaj.wo", "PmdPlus", "0.0.3-SNAPSHOT")
        ));

        if (plugin == null) {
            throw new MojoExecutionException("PMD plugin could not be found");
        }

        executeMojo(
                plugin,
                goal("pmd"),
                configuration(
                        element(name("rulesets"),
                                element( name( "ruleset"), "/rulesets/java/dup.xml"))),
                executionEnvironment(
                        this.mavenProject,
                        this.mavenSession,
                        this.pluginManager
                )
        );
    }
}
