package mods.fossil.fossilAI;

import java.util.Collections;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import mods.fossil.Fossil;
import mods.fossil.entity.mob.EntityDinosaur;
import mods.fossil.guiBlocks.TileEntityFeeder;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class DinoAIEat extends EntityAIBase
{
    private EntityDinosaur Dino;
    private double destX;
    private double destY;
    private double destZ;
    
    private static final int NO_TARGET = -1;
    private static final int ITEM = 1;
    private static final int BLOCK = 2;
    private static final int MOB = 3;
    private static final int FEEDER = 4;
    private int typeofTarget = NO_TARGET;
    private int TimeAtThisTarget = 0;
    
    //the range in which the dino is able to look for items
    private final int SEARCH_RANGE;
    
    //the range the dino is able to get the item when in
    private final int USE_RANGE = 3;
    
    //The item the dino is going to take
    private TileEntityFeeder targetFeeder;
    private EntityItem targetItem;
    private EntityLiving targetMob;
    private DinoAINearestAttackableTargetSorter targetSorter;

    /**
     * Creates The AI, Input: Dino, Speed, searching range
     */
    public DinoAIEat(EntityDinosaur Dino0, int Range0)
    {
    	this.targetItem = null;
    	this.targetMob = null;
    	this.targetFeeder = null;
        this.Dino = Dino0;
        this.setMutexBits(1);
        this.SEARCH_RANGE = Range0;
        this.targetSorter = new DinoAINearestAttackableTargetSorter(this, this.Dino);
        this.TimeAtThisTarget=0;
    }

    public void resetTask()
    {
    	destX = destY = destZ = 0;
        this.Dino.getNavigator().clearPathEntity();
        this.TimeAtThisTarget=0;
    }
    
    
    /**
     * Updates the task.
     */
    public void updateTask()
    {
    	 if( this.Dino.IsHungry() ) // Let's eat
         {
         	// Set our search range.
         	//System.out.println("TargetTypeShould:"+String.valueOf(this.typeofTarget));
         	int Range = this.SEARCH_RANGE;//Current Searching range
         	if(this.Dino.IsDeadlyHungry())
             	Range *= 2;
         	
         	// Find an item to eat.
         	if(!this.Dino.SelfType.FoodItemList.IsEmpty() || !this.Dino.SelfType.FoodBlockList.IsEmpty())// Can Find Items or ItemBlocks!
         	{
 	            Vec3 possibleItemLocation = this.getNearestItem(Range);
 	
 	            if (possibleItemLocation != null)//Found Item, go there and eat it
 	            {
 	                this.destX = possibleItemLocation.xCoord;
 	                this.destY = possibleItemLocation.yCoord;
 	                this.destZ = possibleItemLocation.zCoord;
 	                this.typeofTarget = ITEM;
 	                //System.out.println("ITEM FOUND!");
 	                return;
 	            }
         	}
         	
         	// Eat from the feeder
         	if(this.Dino.SelfType.useFeeder())
         	{
         		this.targetFeeder = this.Dino.GetNearestFeeder(Range/2);
         		if (this.targetFeeder != null)//Found Item, go there and eat it
 	            {
 	                this.destX = this.targetFeeder.xCoord;
                 this.destY = this.targetFeeder.yCoord;
 	                this.destZ = this.targetFeeder.zCoord;
 	                this.typeofTarget=FEEDER;
 	                //System.out.println("FEEDER FOUND!");
 	                return;
 	            }
         	}
         	
         	
             if(!this.Dino.SelfType.FoodBlockList.IsEmpty())//Hasn't found anything and has blocks it can look for
             {
             	Vec3 possibleBlockLocation = this.Dino.getBlockToEat(Range/2);
         		
 	            if (possibleBlockLocation != null)//Found Item, go there and eat it
 	            {
 	                this.destX = possibleBlockLocation.xCoord;
 	                this.destY = possibleBlockLocation.yCoord;
 	                this.destZ = possibleBlockLocation.zCoord;
 	                this.typeofTarget = BLOCK;
 	                //System.out.println("BLOCK FOUND!");
 	                return;
 	            }
             }
             
             // Has not found anything yet, but their food mob list has stuff!
             if(!this.Dino.SelfType.FoodMobList.IsEmpty())
             {
             	Vec3 possiblePreyLocation = this.getNearestPrey(Range);
         		
 	            if (possiblePreyLocation != null)//Found Item, go there and eat it
 	            {
 	                this.destX = possiblePreyLocation.xCoord;
 	                this.destY = possiblePreyLocation.yCoord;
 	                this.destZ = possiblePreyLocation.zCoord;
 	                this.typeofTarget = MOB;
 	                //System.out.println("MOB FOUND!");
 	                return;
 	            }
             }
         }
         else
         {
        	 
         	// If this dino can carry items.
         	if(this.Dino.SelfType.canCarryItems())
         	{
         		//System.out.println("F&A: Dino can carry stuff");
         		
         		int Range = 3; // Dino just steps over an item
         		Vec3 var1 = this.getNearestItem(Range);

                 if (var1 != null)
                 {
                     this.destX = var1.xCoord;
                     this.destY = var1.yCoord;
                     this.destZ = var1.zCoord;
                     this.typeofTarget=ITEM;
                     return;
                 }
                 
         		if( (new Random()).nextInt( (new Random() ).nextInt( 4000 ) +4000 ) == 1 )
         		{
         			// The Dino is willing to (once every 4000-8000 ticks), but looks only in a small radius
 	        		Range=10;
 	        		var1 = this.getNearestItem(Range);
 	
 	                if (var1 != null)
 	                {
 	                    this.destX = var1.xCoord;
 	                    this.destY = var1.yCoord;
 	                    this.destZ = var1.zCoord;
 	                    this.typeofTarget=ITEM;
 	                    return;
 	                }
         		}
         	}
         	else
         	{
         		//System.out.println("F&A: Dino cannot carry anything!");
         	} 	  
       	         	
         }
    }
    
    /**
     * Returns whether the EntityAIBase should begin execution.
     * Called per frame.
     */
public boolean shouldExecute()
    {

    	// Check to see if the dino is hungry
    	// and if one of these are true:
    	// 		1.) it has food in its food list
    	//		2.) it has food blocks in its food block list
    	//		3.) it has food in its mob list
    	//		4.) it can use a feeder
		if( this.Dino.IsHungry() == true )
    	{
    		//System.out.println("F&A: Dino is Hungry!");    		
    		return true;
    	}

		if( this.typeofTarget == MOB && this.targetMob == null )
    	{
    		this.typeofTarget = NO_TARGET;
    		return false;
    	}
    	
    	if( this.typeofTarget == NO_TARGET )
		{
			this.targetItem = null;			
		}
    	
        // Make wander or some other behavior.
		//System.out.println("F&A: Should not execute!");

        this.Dino.getNavigator().clearPathEntity();
        return false;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
    	//System.out.println("Continue:"+String.valueOf(!this.Dino.getNavigator().noPath() && ((this.typeofTarget==ITEM && this.targetItem.isEntityAlive()) || (this.typeofTarget==MOB && this.targetMob.isEntityAlive()) || (this.typeofTarget==FEEDER && !this.targetFeeder.isInvalid()) || (this.typeofTarget==BLOCK && this.Dino.FoodBlockList.CheckBlockById(this.Dino.worldObj.getBlockId((int)destX, (int)destY, (int)destZ))))));
        

    	//System.out.println("F&A:DinoAIEat::continueExecuting()");
    	
    	
    	// Check to see if we have a path
    	if( this.Dino.getNavigator().noPath() == false )
    	{
    		// If this is an item which is alive.
    		if( this.typeofTarget == ITEM && this.targetItem.isEntityAlive() )
    		{
    			//System.out.println("F&A:DinoAIEat::continueExecuting(): Yes");
    			return true;
    		}
    		
    		// If this is a mob and its still alive.
    		if( this.typeofTarget == MOB && this.targetMob.isEntityAlive() )
    		{
    			//System.out.println("F&A:DinoAIEat::continueExecuting(): Yes");
    			return true;
    		}
    		
    		// If this is a valid feeder
    		if( this.typeofTarget == FEEDER && this.targetFeeder.isInvalid() == false )
    		{
    			//System.out.println("F&A:DinoAIEat::continueExecuting(): Yes");
    			return true;
    		}
    		
    		// If this is a block and its in our food block list.
    		if( this.typeofTarget == BLOCK && this.Dino.SelfType.FoodBlockList.CheckBlockById( this.Dino.worldObj.getBlockId( (int)destX, (int)destY, (int)destZ) ) )
    		{    			
    			return true;
    		}
       	}
    	
    	//System.out.println("F&A:DinoAIEat::continueExecuting(): No");
    	
    	// Failed our checks because it has no path or its target is not found/food.
    	this.Dino.getNavigator().clearPathEntity();
    	return false;
    	
    	// HOLY MOTHER OF GOD DONT EVER DO THIS AGAIN!!!!
    	// v										v
    	//
    	// return !this.Dino.getNavigator().noPath() && ((this.typeofTarget==ITEM && this.targetItem.isEntityAlive()) || (this.typeofTarget==MOB && this.targetMob.isEntityAlive()) || (this.typeofTarget==FEEDER && !this.targetFeeder.isInvalid()) || (this.typeofTarget==BLOCK && this.Dino.SelfType.FoodBlockList.CheckBlockById(this.Dino.worldObj.getBlockId((int)destX, (int)destY, (int)destZ))));
    	//
    	// ^										^
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {

    	//System.out.println("F&A: StartExecuting");
    	
    	this.TimeAtThisTarget++;
    	
    	
    	// If we've spent too much time at this target.
    	if( this.TimeAtThisTarget >= 500 )
    	{
    		this.TimeAtThisTarget = 0;
    		this.typeofTarget = NO_TARGET;
    		return;
    	}
    	
    	// Handle eating nothing
    	if( this.typeofTarget == NO_TARGET )
    	{
    		// No target.
        	//System.out.println("F&A: No target to eat.");
    		return;
    	}
    	
    	// Handle eating mobs
    	//System.out.println("TargetType:"+String.valueOf(this.typeofTarget));
    	if( this.typeofTarget == MOB )
    	{
    		// If this mob is not dead
    		if( this.targetMob.isDead == false )
    		{

    	    	//System.out.println("TargetType: Mob is not dead");
    			
    			this.destX=this.targetMob.posX;
    			this.destY=this.targetMob.posY;
    			this.destZ=this.targetMob.posZ;
    			this.Dino.getNavigator().tryMoveToXYZ( this.destX, this.destY, this.destZ, this.Dino.getSpeed() );
    		}
    		else // if the mob is dead.
    		{

    	    	//System.out.println("TargetType: Mob is dead");
    			this.Dino.getNavigator().clearPathEntity();
    			this.TimeAtThisTarget = 0;
                this.typeofTarget = NO_TARGET;
                this.targetMob = null;
    		}
    		return;
    	}
    	
    	// Handle eating everything else
        double Distance = Math.pow(this.Dino.posX - this.destX, 2.0D) + Math.pow(this.Dino.posZ - this.destZ, 2.0D);

        if (Distance < Math.pow(this.USE_RANGE, 2.0D))
        {
        	switch(this.typeofTarget)
            {
            	case ITEM:
	            	if(this.targetItem!=null && !this.targetItem.isDead)
	            	{
	    	            int i=this.Dino.PickUpItem(this.targetItem.getEntityItem());
	    	            if(i>0)
	    	            	this.targetItem.getEntityItem().stackSize=i;
	    	            else
	    	            	this.targetItem.setDead();
	            	}
            	break;
            	
            	case FEEDER:
	            	if(!this.targetFeeder.isInvalid())
	            	{
	            		int healval=MathHelper.floor_double(this.targetFeeder.Feed(this.Dino, this.Dino.SelfType)/15D);
	            		if(Fossil.FossilOptions.Heal_Dinos)
	            			this.Dino.heal(healval);
	            	}
            	break;
            	
            	case BLOCK:
	            	if(Fossil.FossilOptions.Heal_Dinos)
	            		this.Dino.heal(this.Dino.SelfType.FoodBlockList.getBlockHeal(this.Dino.worldObj.getBlockId((int)destX, (int)destY, (int)destZ)));
	            	this.Dino.increaseHunger(this.Dino.SelfType.FoodBlockList.getBlockFood(this.Dino.worldObj.getBlockId((int)destX, (int)destY, (int)destZ)));
	            	this.Dino.worldObj.setBlock((int)destX, (int)destY, (int)destZ,0);
            	break;
            }
        	
            this.Dino.getNavigator().clearPathEntity();
            this.TimeAtThisTarget=0;
            this.typeofTarget=NO_TARGET;
        }
        //else
          //  this.Dino.getNavigator().tryMoveToXYZ(this.destX, this.destY, this.destZ, this.Dino.getSpeed());
    }

    private Vec3 getNearestItem(int SEARCH_RANGE)
    {
        List var1 = this.Dino.worldObj.getEntitiesWithinAABB(EntityItem.class, this.Dino.boundingBox.expand((double)SEARCH_RANGE, 4.0D, (double)SEARCH_RANGE));
        Collections.sort(var1, this.targetSorter);
        Iterator var2 = var1.iterator();
        Vec3 var3 = null;

        while (var2.hasNext())
        {
            EntityItem var4 = (EntityItem)var2.next();

            if (this.Dino.SelfType.FoodItemList.CheckItemById(var4.getEntityItem().itemID) || this.Dino.SelfType.FoodBlockList.CheckBlockById(var4.getEntityItem().itemID) || (this.Dino.SelfType.canCarryItems() && !this.Dino.IsHungry()))
            {//It's food or the dino can carry things and is not hungry
                this.targetItem = var4;
                var3 = Vec3.createVectorHelper(var4.posX, var4.posY, var4.posZ);
                break;
            }
        }
        return var3;
    }
    private Vec3 getNearestPrey(int SEARCH_RANGE)
    {
        List var1 = this.Dino.worldObj.getEntitiesWithinAABB(EntityLiving.class, this.Dino.boundingBox.expand((double)SEARCH_RANGE, 4.0D, (double)SEARCH_RANGE));
        Collections.sort(var1, this.targetSorter);
        Iterator var2 = var1.iterator();
        Vec3 var3 = null;

        while (var2.hasNext())
        {
            EntityLiving var4 = (EntityLiving)var2.next();

            if (this.Dino.SelfType.FoodMobList.CheckMobByClass(var4.getClass()))
            {//It's food
            	if(!(var4 instanceof EntityDinosaur) || (var4 instanceof EntityDinosaur && ((EntityDinosaur) var4).isModelized()==false))
            	{//No modelized Dinos for Lunch!
	                this.targetMob = var4;
	                this.Dino.setAttackTarget(var4);
	                var3 = Vec3.createVectorHelper(var4.posX, var4.posY, var4.posZ);
	                break;
            	}
            }
        }
        return var3;
    }

    /*protected boolean checkTargetReachable(Entity var1, boolean var2)
    {
        if (var1 == null)
        {
            return false;
        }
        else if (!var1.isEntityAlive())
        {
            return false;
        }
        else if (var1.boundingBox.maxY > this.Dino.boundingBox.minY && var1.boundingBox.minY < this.Dino.boundingBox.maxY)
        {
            if (this.Dino.isTamed())
            {
                if (var1 instanceof EntityTameable && ((EntityTameable)var1).isTamed())
                {
                    return false;
                }

                if (var1 == this.entityVar.getOwner())
                {
                    return false;
                }
            }
            else if (var1 instanceof EntityPlayer && !var2 && ((EntityPlayer)var1).capabilities.disableDamage)
            {
                return false;
            }

            return this.Dino.isWithinHomeDistance(MathHelper.floor_double(var1.posX), MathHelper.floor_double(var1.posY), MathHelper.floor_double(var1.posZ));
        }
        else
        {
            return false;
        }
    }*/
}
