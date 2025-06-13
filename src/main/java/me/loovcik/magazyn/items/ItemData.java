package me.loovcik.magazyn.items;

public class ItemData
{
	/** Ilość przedmiotu w magazynie */
	public int amount;

	/** Określa, czy przedmiot jest włączony w magazynie */
	public boolean enabled;

	public ItemData(int amount, boolean enabled){
		this.amount = amount;
		this.enabled = enabled;
	}
}