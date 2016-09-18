package Native.Windows;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface PsApi extends StdCallLibrary {
    PsApi INSTANCE = (PsApi) Native.loadLibrary("psapi", PsApi.class, 
            W32APIOptions.UNICODE_OPTIONS);
    int GetModuleFileNameEx(WinNT.HANDLE hProcess, WinDef.HMODULE hModule, 
            char[] lpFilename, int nSize);
    int GetProcessImageFileName(WinNT.HANDLE hProcess, char[] lpImageFileName, 
            int nSize);
}