# MinetopiaTools

This is a plugin I wrote for a server where they simulate real life in Minecraft.
It adds several functions, the biggest one being CustomItems (me.semx11.mt.customitem). 
You can easily add new CustomItems that have certain triggers (Actions). The following triggers are available:
- **BlockBreakAction**
  
  Allows you to place a CustomItem and break it again, obtaining the same CustomItem with the same NBT data.
- **RightClickItemAction**
  
  Execute code when you right click a CustomItem.
- **RightClickEntityAction**
  
  Execute code when you right click an entity. Used for the Handcuffs in this example.
- **LeftClickEntityAction**
  
  Execute code when you left click (hit) an entity. Used for multiple CustomItems to modify/disable the damage and print things in chat.
  
The most complicated CustomItems are the Handcuffs and the PetTracker. You can find them in CustomItem.java.
