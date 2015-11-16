Instructions
=============
A. If App Manager is configured with BAM, please follow below guidelines.

    1. Stop App Manager, if it is running.

    2. If BAM is not running, start the server.

    3. Log in to Management console. (ex: http://<ip_address>:<port>/carbon)

    4. Go to Home> Manage> BAM Toolbox> List

    5. Uninstall the existing 'App_Manager_Analytics' toolbox.

    6. Go to Home> Manage > Analytics > List and click on 'Add Script'.

    7. Copy and paste 'data_migration_hive_script' to the editor.

    8. Save the script.

    9. Go to Home > Manage > Analytics > List.

    10. Execute newly added script to migrate data. (It will take some time until data migration is completed.)

    11. Go to Home> Manage> BAM Toolbox> List

    12. Click on 'Add New Toolbox'.

    13. Browse new 'App_Manager_Analytics' toolbox and install.(You'll have to wait few seconds until installation is completed.)

    14. Start App Manager

B. If App Manager is not configured with BAM, please follow below guidelines.

    1. Stop App Manager, if it is running.

    2. Execute corresponding db script (h2/mysql/oracle) file in WSO2AM_DB.

    3. Start App Manager.



