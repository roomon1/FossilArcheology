package mod.fossil.common.guiBlocks;

import java.util.Iterator;

import java.util.List;

import mod.fossil.common.Fossil;
import mod.fossil.common.entity.mob.EntityDinosaurce;
import mod.fossil.common.entity.mob.EntityPterosaur;
import mod.fossil.common.entity.mob.EntityRaptor;
import mod.fossil.common.entity.mob.EntityTRex;
import mod.fossil.common.entity.mob.EntityTriceratops;
import mod.fossil.common.fossilEnums.*;
import net.minecraft.tileentity.TileEntity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class TileEntityDrum extends TileEntity
{
    final String DRUM = "Drum.";
    final String MSG = "Msg.";
    final String ORDER = "Order.";
    final String HEAD = "Head";
    final String MIDDLE = "Middle";
    final String TAIL = "Tail";
    final String TREXMSG = "Msg.TRex.";
    final String DINO = "Dino.";
    public EnumOrderType Order;
    public byte note;
    public boolean previousRedstoneState;

    public TileEntityDrum()
    {
        this.Order = EnumOrderType.Stay;
        this.note = 0;
        this.previousRedstoneState = false;
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound var1)
    {
        super.writeToNBT(var1);
        var1.setByte("Order", (byte)this.Order.ordinal());//Fossil.EnumToInt(this.Order));
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound var1)
    {
        super.readFromNBT(var1);
        this.Order = EnumOrderType.values()[var1.getByte("Order")];
    }

    public void triggerNote(World var1, int var2, int var3, int var4)
    {
        if (var1.getBlockMaterial(var2, var3 + 1, var4) == Material.air)
        {
            Material var5 = var1.getBlockMaterial(var2, var3 - 1, var4);
            byte var6 = 0;

            if (var5 == Material.rock)
            {
                var6 = 1;
            }

            if (var5 == Material.sand)
            {
                var6 = 2;
            }

            if (var5 == Material.glass)
            {
                var6 = 3;
            }

            if (var5 == Material.wood)
            {
                var6 = 4;
            }

            var1.addBlockEvent(var2, var3, var4, Fossil.drum.blockID, var6, this.note);
        }
    }

    private String GetOrderString()
    {
        return Fossil.GetLangTextByKey("Order." + this.Order.toString());
    }

    public void TriggerOrder(EntityPlayer var1)
    {
        this.Order = this.Order.Next();
        this.worldObj.playSoundEffect((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, "drum_single", 8.0F, (float)Math.pow(2.0D, (double)(this.Order.ordinal()/*.ToInt() - 1*/)));
        String var2 = Fossil.GetLangTextByKey("Drum.Order.Head");
        String var3 = this.GetOrderString();
        Fossil.ShowMessage(var2 + var3, var1);
        this.onInventoryChanged();
    }

    public boolean SendOrder(int var1, EntityPlayer var2)
    {
        String var3 = "";
        String var4 = "";
        String var5 = Fossil.GetLangTextByKey("Drum.Msg.Head");
        String var6 = Fossil.GetLangTextByKey("Drum.Msg.Middle");
        String var7 = Fossil.GetLangTextByKey("Drum.Msg.Tail");
        this.worldObj.playSoundEffect((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, "drum_triple", 8.0F, (float)Math.pow(2.0D, (double)(this.Order.ordinal()/*ToInt() - 1*/)));

        if (var1 != Item.stick.itemID && var1 != Item.bone.itemID && var1 != Fossil.skullStick.itemID && var1 != Item.arrow.itemID)
        {
            return false;
        }
        else
        {
            if (var1 == Item.stick.itemID)
            {
                this.OrderTri();
                var3 = EntityDinosaurce.GetNameByEnum(EnumDinoType.Triceratops, true);
            }

            if (var1 == Item.bone.itemID)
            {
                this.OrderRaptor();
                var3 = EntityDinosaurce.GetNameByEnum(EnumDinoType.Raptor, true);
            }

            if (var1 == Item.arrow.itemID)
            {
                this.OrderPTS();
                var3 = EntityDinosaurce.GetNameByEnum(EnumDinoType.Pterosaur, true);
            }

            if (var1 == Fossil.skullStick.itemID)
            {
                this.OrderTRex(var2);
            }

            var4 = this.GetOrderString();

            if (var1 != Fossil.skullStick.itemID)
            {
                Fossil.ShowMessage(var5 + var3 + var6 + var4 + var7, var2);
                return true;
            }
            else
            {
                String var8 = Fossil.GetLangTextByKey("Drum.Msg.TRex." + String.valueOf(this.Order.ordinal()/*ToInt() + 1*/));
                Fossil.ShowMessage(var8, var2);
                return true;
            }
        }
    }

    private void OrderRaptor()
    {
        List var1 = this.worldObj.getEntitiesWithinAABB(EntityRaptor.class, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)this.xCoord + 1.0D, (double)this.yCoord + 1.0D, (double)this.zCoord + 1.0D).expand(30.0D, 4.0D, 30.0D));
        Iterator var2 = var1.iterator();

        while (var2.hasNext())
        {
            Entity var3 = (Entity)var2.next();
            EntityDinosaurce var4 = (EntityDinosaurce)var3;

            if (var4.isTamed())
            {
                var4.SetOrder(this.Order);
            }
        }
    }

    private void OrderPTS()
    {
        List var1 = this.worldObj.getEntitiesWithinAABB(EntityPterosaur.class, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)this.xCoord + 1.0D, (double)this.yCoord + 1.0D, (double)this.zCoord + 1.0D).expand(30.0D, 4.0D, 30.0D));
        Iterator var2 = var1.iterator();

        while (var2.hasNext())
        {
            Entity var3 = (Entity)var2.next();
            EntityDinosaurce var4 = (EntityDinosaurce)var3;

            if (var4.isTamed())
            {
                var4.SetOrder(this.Order);
            }
        }
    }

    private void OrderTri()
    {
        List var1 = this.worldObj.getEntitiesWithinAABB(EntityTriceratops.class, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)this.xCoord + 1.0D, (double)this.yCoord + 1.0D, (double)this.zCoord + 1.0D).expand(30.0D, 4.0D, 30.0D));
        Iterator var2 = var1.iterator();

        while (var2.hasNext())
        {
            Entity var3 = (Entity)var2.next();
            EntityDinosaurce var4 = (EntityDinosaurce)var3;

            if (var4.isTamed())
            {
                var4.SetOrder(this.Order);
            }
        }
    }

    private void OrderTRex(EntityPlayer var1)
    {
        List var2 = this.worldObj.getEntitiesWithinAABB(EntityTRex.class, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)this.xCoord + 1.0D, (double)this.yCoord + 1.0D, (double)this.zCoord + 1.0D).expand(50.0D, 4.0D, 50.0D));
        Iterator var3 = var2.iterator();

        while (var3.hasNext())
        {
            Entity var4 = (Entity)var3.next();
            EntityTRex var5 = (EntityTRex)var4;

            if (var5.getDinoAge() >= 3 && !var5.isTamed())
            {
                var5.setSelfAngry(true);
                var5.setAttackTarget(var1);
            }
        }
    }
}