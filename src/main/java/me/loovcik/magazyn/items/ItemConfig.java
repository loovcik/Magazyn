package me.loovcik.magazyn.items;

import org.bukkit.Material;

public class ItemConfig
{
	private final Material material;
	private String name;
	private int slot;
	private double price;
	private int stackSize;

	/** Material w magazynie **/
	public Material getMaterial(){ return material; }

	/** Pobiera nazwę przedmiotu w magazynie */
	public String getName(){ return name; }

	/** Ustawia własną nazwę przedmiotu w magazynie<br><br>
	 * (używane do wyświetlania na czacie)
	 */
	public void setName(String name) { this.name = name; }

	/** Pobiera numer slotu w gui **/
	public int getSlot(){ return slot; }

	/** Ustawia numer slotu w gui **/
	public void setSlot(int value) { slot = value; }

	/** Pobiera aktualną cenę przedmiotu **/
	public double getPrice(){ return price; }

	/** Ustawia cenę sprzedaży przedmiotu przez magazyn **/
	public void setPrice(float value) { price = value; }

	/** Pobiera wielkość staku, po której przedmiot zostanie<br>
	 * zabrany z eq gracza
	 */
	public int getStackSize(){ return stackSize; }

	/**
	 * Ustawia wielkość staku, po której przedmiot zostanie<br>
	 * zabrany z ekwipunku gracza
	 */
	public void setStackSize(int value) { stackSize = value; }

	/** Ustawia cenę sprzedaży przedmiotu przez magazyn **/
	public void setPrice(String fromConfig){
		if (fromConfig == null) return;
		String[] parts = fromConfig.split("/");
		if (parts.length == 2){
			stackSize = Integer.parseInt(parts[1].trim());
			price = Float.parseFloat(parts[0].trim()) / stackSize;
		}
	}

	/** Ustawia cenę sprzedaży przedmiotu przez magazyn **/
	public void setPrice(float price, int stackSize){
		this.price = price;
		this.stackSize = stackSize;
	}

	public ItemConfig(Material material){
		this.material = material;
	}
}