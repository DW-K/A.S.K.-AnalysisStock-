import subprocess


def subProcess32(func, arg=[]):
    env = {'ALLUSERSPROFILE': 'C:\\ProgramData', 'APPDATA': 'C:\\Users\\pch14\\AppData\\Roaming',
           'COMMONPROGRAMFILES': 'C:\\Program Files (x86)\\Common Files',
           'COMMONPROGRAMFILES(X86)': 'C:\\Program Files (x86)\\Common Files',
           'COMMONPROGRAMW6432': 'C:\\Program Files\\Common Files', 'COMPUTERNAME': 'DODOCOM',
           'COMSPEC': 'C:\\Windows\\system32\\cmd.exe', 'CONDA_DEFAULT_ENV': 'project_ask_32',
           'CONDA_PREFIX': 'D:\\IDE\\anaconda3\\envs\\project_ask_32', 'CONDA_PROMPT_MODIFIER': '(project_ask_32) ',
           'CONDA_SHLVL': '1', 'CUDA_PATH': 'C:\\Program Files\\NVIDIA GPU Computing Toolkit\\CUDA\\v11.1',
           'CUDA_PATH_V11_1': 'C:\\Program Files\\NVIDIA GPU Computing Toolkit\\CUDA\\v11.1',
           'DRIVERDATA': 'C:\\Windows\\System32\\Drivers\\DriverData', 'HOMEDRIVE': 'C:', 'HOMEPATH': '\\Users\\pch14',
           'IDEA_INITIAL_DIRECTORY': 'C:\\Windows\\system32', 'JAVA_HOME': 'C:\\Program Files\\Java\\jdk-16.0.1',
           'LOCALAPPDATA': 'C:\\Users\\pch14\\AppData\\Local', 'LOGONSERVER': '\\\\DODOCOM',
           'NUMBER_OF_PROCESSORS': '8',
           'NVCUDASAMPLES11_1_ROOT': 'C:\\ProgramData\\NVIDIA Corporation\\CUDA Samples\\v11.1',
           'NVCUDASAMPLES_ROOT': 'C:\\ProgramData\\NVIDIA Corporation\\CUDA Samples\\v11.1',
           'NVTOOLSEXT_PATH': 'C:\\Program Files\\NVIDIA Corporation\\NvToolsExt\\',
           'ONEDRIVE': 'D:\\OneDrive - gc.gachon.ac.kr', 'ONEDRIVECOMMERCIAL': 'D:\\OneDrive - gc.gachon.ac.kr',
           'ONEDRIVECONSUMER': 'D:\\OneDrive', 'OS': 'Windows_NT',
           'PATH': 'D:\\IDE\\anaconda3\\envs\\project_ask_32\\lib\\site-packages\\PyQt5\\Qt\\bin;D:\\IDE\\anaconda3\\envs\\project_ask_32;D:\\IDE\\anaconda3\\envs\\project_ask_32\\Library\\mingw-w64\\bin;D:\\IDE\\anaconda3\\envs\\project_ask_32\\Library\\usr\\bin;D:\\IDE\\anaconda3\\envs\\project_ask_32\\Library\\bin;D:\\IDE\\anaconda3\\envs\\project_ask_32\\Scripts;D:\\IDE\\anaconda3\\envs\\project_ask_32\\bin;D:\\IDE\\anaconda3\\condabin;C:\\Program Files\\NVIDIA GPU Computing Toolkit\\CUDA\\v11.1\\bin;C:\\Program Files\\NVIDIA GPU Computing Toolkit\\CUDA\\v11.1\\libnvvp;C:\\Users\\pch14\\AppData\\Local\\Programs\\Python\\Python38\\Scripts;C:\\Users\\pch14\\AppData\\Local\\Programs\\Python\\Python38;C:\\Program Files\\Common Files\\Oracle\\Java\\javapath;C:\\Program Files\\Python38\\Scripts;C:\\Program Files\\Python38;C:\\Program Files (x86)\\Intel\\iCLS Client;D:\\IDE\\Anaconda3;D:\\IDE\\Anaconda3\\Library\\mingw-w64\\bin;D:\\IDE\\Anaconda3\\Library\\usr\\bin;D:\\IDE\\Anaconda3\\Library\\bin;D:\\IDE\\Anaconda3\\Scripts;C:\\Program Files\\Python39\\Scripts;C:\\Program Files\\Python39;C:\\Program Files\\Intel\\iCLS Client;C:\\Windows\\system32;C:\\Windows;C:\\Windows\\System32\\Wbem;C:\\Windows\\System32\\WindowsPowerShell\\v1.0;C:\\Windows\\System32\\OpenSSH;C:\\Program Files\\Intel\\WiFi\\bin;C:\\Program Files\\Common Files\\Intel\\WirelessCommon;C:\\Program Files (x86)\\NVIDIA Corporation\\PhysX\\Common;C:\\Program Files\\NVIDIA Corporation\\NVIDIA NvDLISR;C:\\Program Files (x86)\\dotnet;C:\\Program Files\\dotnet;C:\\Program Files\\Microsoft SQL Server\\130\\Tools\\Binn;C:\\Program Files\\Microsoft SQL Server\\Client SDK\\ODBC\\170\\Tools\\Binn;C:\\Program Files (x86)\\Microsoft SQL Server\\150\\DTS\\Binn;C:\\Program Files\\Azure Data Studio\\bin;C:\\Program Files\\nodejs;C:\\Program Files (x86)\\Intel\\Intel(R) Management Engine Components\\DAL;C:\\Program Files\\Intel\\Intel(R) Management Engine Components\\DAL;C:\\Program Files (x86)\\Intel\\Intel(R) Management Engine Components\\IPT;C:\\Program Files\\Intel\\Intel(R) Management Engine Components\\IPT;C:\\MinGW\\bin;C:\\Program Files\\Java\\jdk-16.0.1\\bin;C:\\Program Files\\NVIDIA Corporation\\Nsight Compute 2020.2.0;C:\\Program Files\\Git\\cmd;D:\\Program Files\\Tesseract-OCR;D:\\IDE\\anaconda3;D:\\IDE\\anaconda3\\Library\\mingw-w64\\bin;D:\\IDE\\anaconda3\\Library\\usr\\bin;D:\\IDE\\anaconda3\\Library\\bin;D:\\IDE\\anaconda3\\Scripts;C:\\Users\\pch14\\AppData\\Local\\Microsoft\\WindowsApps;D:\\Program Files\\Bandizip;C:\\Users\\pch14\\.dotnet\\tools;D:\\IDE\\Microsoft VS Code\\bin;C:\\Users\\pch14\\AppData\\Roaming\\npm;D:\\IDE\\PyCharm Community Edition 2021.1.1\\bin;D:\\IDE\\PyCharm 2021.1.1\\bin;C:\\Program Files\\Java\\jdk-16.0.1bin;D:\\IDE\\PyCharm Edu 2021.1\\bin;C:\\Program Files\\NVIDIA GPU Computing Toolkit\\CUDA\\v11.1\\extras\\CUPTI\\lib64;C:\\Program Files\\NVIDIA GPU Computing Toolkit\\CUDA\\v11.1\\include',
           'PATHEXT': '.COM;.EXE;.BAT;.CMD;.VBS;.VBE;.JS;.JSE;.WSF;.WSH;.MSC;.PY;.PYW', 'PROCESSOR_ARCHITECTURE': 'x86',
           'PROCESSOR_ARCHITEW6432': 'AMD64',
           'PROCESSOR_IDENTIFIER': 'Intel64 Family 6 Model 94 Stepping 3, GenuineIntel', 'PROCESSOR_LEVEL': '6',
           'PROCESSOR_REVISION': '5e03', 'PROGRAMDATA': 'C:\\ProgramData', 'PROGRAMFILES': 'C:\\Program Files (x86)',
           'PROGRAMFILES(X86)': 'C:\\Program Files (x86)', 'PROGRAMW6432': 'C:\\Program Files',
           'PROMPT': '(project_ask_32) $P$G',
           'PSMODULEPATH': 'C:\\Program Files\\WindowsPowerShell\\Modules;C:\\Windows\\system32\\WindowsPowerShell\\v1.0\\Modules',
           'PUBLIC': 'C:\\Users\\Public', 'PYCHARM': 'D:\\IDE\\PyCharm 2021.1.1\\bin;',
           'PYCHARM COMMUNITY EDITION': 'D:\\IDE\\PyCharm Community Edition 2021.1.1\\bin;',
           'PYCHARM EDU': 'D:\\IDE\\PyCharm Edu 2021.1\\bin;', 'PYCHARM_HOSTED': '1', 'PYTHONIOENCODING': 'UTF-8',
           'PYTHONPATH': 'D:\\IDE\\workspace\\ASK\\project_ask_32', 'PYTHONUNBUFFERED': '1', 'SYSTEMDRIVE': 'C:',
           'SYSTEMROOT': 'C:\\Windows', 'TEMP': 'C:\\Users\\pch14\\AppData\\Local\\Temp',
           'TMP': 'C:\\Users\\pch14\\AppData\\Local\\Temp', 'USERDOMAIN': 'DODOCOM',
           'USERDOMAIN_ROAMINGPROFILE': 'DODOCOM', 'USERNAME': 'pch14', 'USERPROFILE': 'C:\\Users\\pch14',
           'WINDIR': 'C:\\Windows'}

    interpreter = r'D:\IDE\anaconda3\envs\project_ask_32\python.exe'

    kwargs = {"stdin": subprocess.PIPE, "stdout": subprocess.PIPE, "env": env}
    with subprocess.Popen([interpreter, fr'..\project_ask_32\{func}.py'] + arg, **kwargs,
                          shell=True) as proc:
        out, err = proc.communicate()
        if err is not None:
            print(err)

        print(out.decode())

    # return out.decode()


def getStockPrice(date=None):
    if date is None:
        subProcess32('getStockPrice')
    else:
        subProcess32('getStockPrice', arg=date)
    print("complete crawling stock")


if __name__ == "__main__":
    subProcess32('getStock')
