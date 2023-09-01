package liquibase.extension.testing.command

import liquibase.exception.CommandValidationException

CommandTests.define {
    command = ["updateCountSql"]
    signature = """
Short Description: Generate the SQL to deploy the specified number of changes
Long Description: NOT SET
Required Args:
  changelogFile (String) The root changelog
  count (Integer) The number of changes to generate SQL for
  url (String) The JDBC database connection URL
    OBFUSCATED
Optional Args:
  changeExecListenerClass (String) Fully-qualified class which specifies a ChangeExecListener
    Default: null
  changeExecListenerPropertiesFile (String) Path to a properties file for the ChangeExecListenerClass
    Default: null
  contextFilter (String) Changeset contexts to match
    Default: null
  defaultCatalogName (String) The default catalog name to use for the database connection
    Default: null
  defaultSchemaName (String) The default schema name to use for the database connection
    Default: null
  driver (String) The JDBC driver class
    Default: null
  driverPropertiesFile (String) The JDBC driver properties file
    Default: null
  labelFilter (String) Changeset labels to match
    Default: null
  outputDefaultCatalog (Boolean) Control whether names of objects in the default catalog are fully qualified or not. If true they are. If false, only objects outside the default catalog are fully qualified
    Default: true
  outputDefaultSchema (Boolean) Control whether names of objects in the default schema are fully qualified or not. If true they are. If false, only objects outside the default schema are fully qualified
    Default: true
  password (String) Password to use to connect to the database
    Default: null
    OBFUSCATED
  username (String) Username to use to connect to the database
    Default: null
"""

    run "Happy path", {
        arguments = [
                url:      { it.url },
                username: { it.username },
                password: { it.password },
                count        : 1,
                changelogFile: "changelogs/h2/complete/simple.changelog.xml",
        ]

        expectedResults = [
                statusCode   : 0,
                defaultChangeExecListener: 'not_null',
                updateReport: 'not_null'
        ]

        expectedOutput = [
                "UPDATE PUBLIC.DATABASECHANGELOGLOCK SET LOCKED = TRUE",
                """-- Changeset changelogs/h2/complete/simple.changelog.xml::1::nvoxland
-- You can add comments to changeSets.
--             They can even be multiple lines if you would like.
--             They aren't used to compute the changeSet MD5Sum, so you can update them whenever you want without causing problems.
CREATE TABLE PUBLIC.person (id INT GENERATED BY DEFAULT AS IDENTITY NOT NULL, firstname VARCHAR(50), lastname VARCHAR(50) NOT NULL, CONSTRAINT PK_PERSON PRIMARY KEY (id));

INSERT INTO PUBLIC.DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('1', 'nvoxland', 'changelogs/h2/complete/simple.changelog.xml', NOW(), 1, '9:10de8cd690aed1d88d837cbe555d1684', 'createTable tableName=person', 'You can add comments to changeSets.
            They can even be multiple lines if you would like.
            They aren''t used to compute the changeSet MD5Sum, so you can update them whenever you want without causing problems.', 'EXECUTED', NULL, NULL, 'DEV'
"""
        ]
    }


    run "Happy path with an output file", {
        arguments = [
                url:      { it.url },
                username: { it.username },
                password: { it.password },
                count        : 1,
                changelogFile: "changelogs/h2/complete/simple.changelog.xml",
        ]

        setup {
            cleanResources("target/test-classes/updateCount.sql")
        }

        outputFile = new File("target/test-classes/updateCount.sql")

        expectedFileContent = [
                //
                // Find the " -- Release Database Lock" line
                //
                "target/test-classes/updateCount.sql" : [CommandTests.assertContains("-- Release Database Lock")]
        ]

        expectedResults = [
                statusCode   : 0,
                defaultChangeExecListener: 'not_null',
                updateReport: 'not_null'
        ]
    }

    run "Run without a URL throws an exception", {
        arguments = [
                url: "",
                count : 1
        ]
        expectedException = CommandValidationException.class
    }

    run "Run without a changeLogFile throws an exception", {
        arguments = [
                changelogFile: "",
                count: 1
        ]
        expectedException = CommandValidationException.class
    }

    run "Run without count throws an exception", {
        arguments = [
                changelogFile: "changelogs/h2/complete/simple.changelog.xml"
        ]
        expectedException = CommandValidationException.class
    }

    run "Run without any argument throws an exception", {
        arguments = [
                url: "",
        ]
        expectedException = CommandValidationException.class
    }
}
