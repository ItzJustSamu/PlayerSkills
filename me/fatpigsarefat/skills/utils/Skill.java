/*    */ package me.fatpigsarefat.skills.utils;
/*    */ 
/*    */ public enum Skill
/*    */ {
/*  5 */   STRENGTH,
/*  6 */   CRITICALS,
/*  7 */   RESISTANCE,
/*  8 */   ARCHERY,
/*  9 */   HEALTH;
/*    */   
/*    */   public static Skill getSkillByName(String s) {
/* 12 */     switch (s) {
/*    */       case "strength":
/* 14 */         return STRENGTH;
/*    */       
/*    */       case "criticals":
/* 17 */         return CRITICALS;
/*    */       
/*    */       case "resistance":
/* 20 */         return RESISTANCE;
/*    */       
/*    */       case "archery":
/* 23 */         return ARCHERY;
/*    */       
/*    */       case "health":
/* 26 */         return HEALTH;
/*    */     } 
/*    */     
/* 29 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\jessl\Downloads\PlayerSkills.jar!\me\fatpigsarefat\skill\\utils\Skill.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */