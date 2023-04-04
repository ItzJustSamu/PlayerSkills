/*    */ package me.fatpigsarefat.skills.utils;
/*    */ 
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.Location;
/*    */ 
/*    */ public class LocationUtil
/*    */ {
/*    */   public static Location toLocation(String str) {
/*  9 */     String[] str2loc = str.split(":");
/* 10 */     if (str2loc.length == 4) {
/* 11 */       Location loc = new Location(Bukkit.getServer().getWorld(str2loc[0]), 0.0D, 0.0D, 0.0D);
/* 12 */       loc.setX(Double.parseDouble(str2loc[1]));
/* 13 */       loc.setY(Double.parseDouble(str2loc[2]));
/* 14 */       loc.setZ(Double.parseDouble(str2loc[3]));
/* 15 */       return loc;
/*    */     } 
/* 17 */     return null;
/*    */   }
/*    */   
/*    */   public static String toString(Location loc) {
/* 21 */     return loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ();
/*    */   }
/*    */ }


/* Location:              C:\Users\jessl\Downloads\PlayerSkills.jar!\me\fatpigsarefat\skill\\utils\LocationUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */