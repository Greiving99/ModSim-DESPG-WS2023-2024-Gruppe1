Instructions for setting up JavaFX: http://www.cs.umd.edu/~nelson/eclipse/javafx/


Runtime Parameters


--module-path <PATH_TO_JAVAFX_SDK/lib> --add-modules javafx.controls,javafx.fxml

for example:
--module-path /Library/Java/javafx-sdk-20.0.1/lib --add-modules javafx.controls,javafx.fxml


Errors
-------
JavaFX runtime components are missing, and are required to run this application
 - Check that the VM argument is added for each program you're trying to run.
   The process can be simplified by running all the programs, then going to Run Configurations,
   and on the left side under Java Application, you'll see the launch configurations listed for all the examples.
   After selecting the Arguments tab for one program, you can click on each example and copy/paste the VM arguments.

<class name> can't be resolved to a type
 - Import statements might be missing; if you expand the import statements in the GUI Code examples,
   using the + by the line numbers, you'll see all the necessary imports which you can copy into your project.

Module javafx.controls not found
 - Check that the JavaFX user library you created is under the Classpath (project > properties > java build path).
 - Make sure you have --module-path and --add-modules=javafx.controls before and after the path, respectively,
   and that the path matches the location of the JavaFX lib folder.
 - Sometimes fixing the VM argument and clicking apply doesn't resolve the error. In this case, you may need to
   delete the existing launch configurations listed under Java Application in the Run Configurations window,
   and then go through the process of running the program(s) and adding the VM argument again.

Graphics Device initialization failed for: es2, sw
Error initializing QuantumRenderer: no suitable pipeline found
 - For M1 Macs, this can occur when the JRE and JavaFX SDK are incompatible (such as using an old/non-AArch64 JRE).
   Check that the JRE System Library version in the project folder is correct. If it's not, you can go to
   project > properties > java build path, select the undesired library and remove it; then select
   Modulepath > Add Library > JRE System Library, select the Workspace default and finish.
   You may need to rebuild the project (project > clean) after this.
 - If the above steps didn't work and you downloaded the AArch64/ARM64 JavaFX version,
   downloading the x64 version instead can fix the error.
 - Another possible solution is to delete the created JavaFX library, redownload it, and go through the steps again.

Could not find or load main class <path1> ... Caused by: java.lang.ClassNotFoundException: <path2>
 - This can happen if you have spaces in the JavaFX library path; you can fix it by adding quotes around the path,
   e.g., --module-path "C:\...\javafx-sdk-#\lib" --add-modules=javafx.controls

<path/to/lib> Operation not permitted
 - On Macs, Eclipse may not have access to where the lib folder is stored. Go to Apple menu > System Preferences,
   click Security & Privacy, then click Privacy. If the lib folder is in your downloads folder,
   you can select Files and Folders, and under Eclipse, check Downloads Folder.