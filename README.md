# tenkaichi-scouter-detector
A **command-line** modding tool (for the **PS2 & Wii** versions of DBZ Budokai **Tenkaichi 2 & 3**)
that detects the **presence of scouters and Z-Search parameters**, in a folder containing **character costume files**.

The program also provides the option (via the ``-w`` argument) to **fix** said PAK files' **Z-Search parameters in case they are invalid**.

**NOTE:** As of ``v1.0``, this feature has yet to be tested.

Otherwise, the detection process (via the ``-r`` argument) **saves a CSV displaying the Z-Search parameters and additional validations**
in the user's default directory (in Windows, their ``Documents`` folder).