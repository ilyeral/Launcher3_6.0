package com.android.launcher3;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.util.LongArrayMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.android.launcher3.LauncherAppState.getLauncherProvider;
import static com.android.launcher3.LauncherModel.addItemToDatabase;

/**
 * Created by 25077 on 2019/2/14.
 */

public class BackupInfoUtil {
    static LauncherProvider launcherProvider=getLauncherProvider();
    static LauncherAppState launcherAppState=LauncherAppState.getInstance();
    static LauncherModel model=launcherAppState.getModel();


    public static String BackupIconInfo(){
        final Uri uri1 = LauncherSettings.WorkspaceScreens.CONTENT_URI;
        final Uri uri2 = LauncherSettings.Favorites.CONTENT_URI;
        Cursor c_workspace=launcherProvider.query(uri1,null,null,null,"screenRank");
        Cursor c_favorites=launcherProvider.query(uri2,null,null,null,null);
        ArrayList<IconInfoTemp> iconInfoTempList=new ArrayList<IconInfoTemp>();
        while (c_favorites.moveToNext()) {
            IconInfoTemp item=new IconInfoTemp();
            item.id = c_favorites.getInt(c_favorites.getColumnIndex("_id"));
            item.title = c_favorites.getString(c_favorites.getColumnIndex("title"));
            item.intent=c_favorites.getString(c_favorites.getColumnIndex("intent"));
            item.container = c_favorites.getInt(c_favorites.getColumnIndex("container"));
            item.screenId = c_favorites.getInt(c_favorites.getColumnIndex("screen"));
            item.cellX = c_favorites.getInt(c_favorites.getColumnIndex("cellX"));
            item.cellY = c_favorites.getInt(c_favorites.getColumnIndex("cellY"));
            item.spanX = c_favorites.getInt(c_favorites.getColumnIndex("spanX"));
            item.spanY = c_favorites.getInt(c_favorites.getColumnIndex("spanY"));
            item.itemType = c_favorites.getInt(c_favorites.getColumnIndex("itemType"));
            item.iconType= c_favorites.getInt(c_favorites.getColumnIndex("iconType"));
            item.iconPackage= c_favorites.getString(c_favorites.getColumnIndex("iconPackage"));
            item.iconResource= c_favorites.getString(c_favorites.getColumnIndex("iconResource"));
            item.modified= c_favorites.getInt(c_favorites.getColumnIndex("modified"));
            item.restored= c_favorites.getInt(c_favorites.getColumnIndex("restored"));
            item.profileId= c_favorites.getInt(c_favorites.getColumnIndex("profileId"));
            item.rank= c_favorites.getInt(c_favorites.getColumnIndex("rank"));

            item.appWidgetId= c_favorites.getInt(c_favorites.getColumnIndex("appWidgetId"));
            item.isShortcut= c_favorites.getInt(c_favorites.getColumnIndex("isShortcut"));
            item.appWidgetProvider= c_favorites.getString(c_favorites.getColumnIndex("appWidgetProvider"));
//             options
            //item.dropPos=null;
            iconInfoTempList.add(item);
            //Log.e("Gson",""+item.toString());
        }
        Gson gson = new Gson();
        String iconInfoJson=gson.toJson(iconInfoTempList);
        return iconInfoJson;
//        String str="CREATE TABLE favorites (" +
//                "_id INTEGER PRIMARY KEY," +
//                "title TEXT," +
//                "intent TEXT," +
//                "container INTEGER," +
//                "screen INTEGER," +
//                "cellX INTEGER," +
//                "cellY INTEGER," +
//                "spanX INTEGER," +
//                "spanY INTEGER," +
//                "itemType INTEGER," +
//                "appWidgetId INTEGER NOT NULL DEFAULT -1," +
//                "isShortcut INTEGER," +
//                "iconType INTEGER," +
//                "iconPackage TEXT," +
//                "iconResource TEXT," +
//                "icon BLOB," +
//                "uri TEXT," +
//                "displayMode INTEGER," +
//                "appWidgetProvider TEXT," +
//                "modified INTEGER NOT NULL DEFAULT 0," +
//                "restored INTEGER NOT NULL DEFAULT 0," +
//                "profileId INTEGER DEFAULT " + userSerialNumber + "," +
//                "rank INTEGER NOT NULL DEFAULT 0," +
//                "options INTEGER NOT NULL DEFAULT 0" +
//                ");";
    }

    public static void recoverIconInfo(String str){
        launcherProvider.ClearDB();
        model.clearSBG();
        //Log.e("str",""+str);
        ArrayList<IconInfoTemp> iconInfoTempList = getIconListFromStr(str);

        ArrayList<ItemInfo> iconInfoList= getIconInfoFromTemp(iconInfoTempList);

        insertFavorites(iconInfoList);

        model.forceReload();
    }
    public static void insertFavorites(ArrayList<ItemInfo> shortcutInfos){
        final PackageManager packageManager = launcherAppState.getContext().getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if(!shortcutInfos.isEmpty()){
            ArrayList<ItemInfo> folderList=new ArrayList<ItemInfo>();
            for(ItemInfo item : shortcutInfos){
                if(item.itemType==2){
                    ItemInfo folderItem=new ItemInfo();
                    folderItem.id=item.id;
                    folderList.add(folderItem);
                    item.id=-1;
                    //Log.e("backup msg","Folder:"+item.toString());
                    addItemToDatabase(launcherAppState.getContext(),item,item.container,item.screenId,item.cellX,item.cellY);
                }
            }

            for(ItemInfo item : shortcutInfos){
                if(item.itemType!=2) {
                    if(item.container>0){
                        int i=0;
                        for(ItemInfo folder : folderList){
                            i++;
                            if(item.container==folder.id){
                                item.container=i;
                            }
                        }
                    }
                    if(item.itemType==0){
                        String str=item.getIntent().toString();
                        String packageName=str.split("cmp=")[1].split("/")[0];
                        if(isAvilible(packageName,pinfo)){
                            item.id=-1;
                            //Log.e("backup msg","ShortCut:"+item.toString());
                            addItemToDatabase(launcherAppState.getContext(), item, item.container, item.screenId, item.cellX, item.cellY);
                        }
                    }
                }
            }
        }
    }
    static ArrayList<IconInfoTemp> getIconListFromStr(String info){
        Gson gson = new Gson();
        return gson.fromJson(info,new TypeToken<ArrayList<IconInfoTemp>>(){}.getType());
    }
    static ArrayList<ItemInfo> getIconInfoFromTemp(ArrayList<IconInfoTemp> temp){
        ArrayList<ItemInfo> IconInfos=new ArrayList<ItemInfo>();
        for(IconInfoTemp mtemp : temp){
            if(mtemp.itemType==2){
                FolderInfo iconInfo;
                iconInfo = new FolderInfo();
                iconInfo.id = mtemp.id;
                iconInfo.title = mtemp.title;
                iconInfo.container = mtemp.container;
                iconInfo.screenId = mtemp.screenId;
                iconInfo.cellX = mtemp.cellX;
                iconInfo.cellY = mtemp.cellY;
                iconInfo.spanX = mtemp.spanX;
                iconInfo.spanY = mtemp.spanY;
                iconInfo.itemType = mtemp.itemType;
                iconInfo.rank = mtemp.rank;
                iconInfo.dropPos=null;
                IconInfos.add(iconInfo);
            }else if(mtemp.itemType==4){
                LauncherAppWidgetInfo iconInfo;
                String[] strList=mtemp.appWidgetProvider.split("/");
                iconInfo = new LauncherAppWidgetInfo(mtemp.appWidgetId,new ComponentName(strList[0],strList[1]));
                iconInfo.id = mtemp.id;
                iconInfo.title = mtemp.title;
                iconInfo.container = mtemp.container;
                iconInfo.screenId = mtemp.screenId;
                iconInfo.cellX = mtemp.cellX;
                iconInfo.cellY = mtemp.cellY;
                iconInfo.spanX = mtemp.spanX;
                iconInfo.spanY = mtemp.spanY;
                iconInfo.itemType = mtemp.itemType;
                iconInfo.rank = mtemp.rank;
                iconInfo.dropPos=null;
                IconInfos.add(iconInfo);
            }else{
                ShortcutInfo iconInfo;
                iconInfo=new ShortcutInfo();
                iconInfo.id = -1;
                iconInfo.title = mtemp.title;

                Intent intent=null;
                if(!(iconInfo.title==null||iconInfo.title.equals(""))){
                    try {
                        intent = Intent.parseUri(mtemp.intent, 0);
                    } catch (URISyntaxException e) {
                    }
                }
                iconInfo.intent=intent;
                iconInfo.container = mtemp.container;
                iconInfo.screenId = mtemp.screenId;
                iconInfo.cellX = mtemp.cellX;
                iconInfo.cellY = mtemp.cellY;
                iconInfo.spanX = mtemp.spanX;
                iconInfo.spanY = mtemp.spanY;
                iconInfo.itemType = mtemp.itemType;
                iconInfo.dropPos=null;
                IconInfos.add(iconInfo);
            }
        }
        return IconInfos;
    }
    static private boolean isAvilible( String packageName ,List<PackageInfo> pinfo){

        for ( int i = 0; i < pinfo.size(); i++ )        {
            if(pinfo.get(i).packageName.equalsIgnoreCase(packageName)){
                pinfo.remove(pinfo.get(i));
                return true;
            }
        }
        return false;
    }

    static class IconInfoTemp {
        int id;
        String title;
        String intent;
        int container;
        int screenId;
        int cellX;
        int cellY;
        int spanX;
        int spanY;
        int itemType;
        int iconType;
        String iconPackage;
        String iconResource;
        int modified;
        int restored;
        int profileId;
        int rank;

        int appWidgetId;
        int isShortcut;
        String appWidgetProvider;
        public IconInfoTemp(){
            id=-1;
            title=null;
            intent=null;
            container=-1;
            screenId=-1;
            cellX=-1;
            cellY=-1;
            spanX=-1;
            spanY=-1;
            itemType=-1;
        }
        public String toString(){
            return ""
                    +"  id:"+id
                    +"  title:"+title
                    +"  screenId:"+screenId
                    +"  container:"+container
                    +"  itemType:"+itemType
                    +"  iconType:"+iconType
                    +"  iconPackage:"+iconPackage
                    +"  iconResource:"+iconResource
                    +"  modified:"+modified
                    +"  restored:"+restored
                    +"  restored:"+restored
                    +"  profileId:"+profileId
                    +"  rank:"+rank;
        }
    }

}
