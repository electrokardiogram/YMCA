package thesis;

public class WeightedCollege {
	
	private String name;
	private int id;
	private int weight;
	private int classSize;
	private int min;
	
	public WeightedCollege(String _name, int _id, int _weight, int _classSize)
	{
		name = _name;
		id = _id;
		weight = _weight;
		classSize = _classSize;
	}
	
	public WeightedCollege(int _id, int _weight)
	{
		Integer temp = _id;
		name = temp.toString();
		id = _id;
		weight = _weight;
	}
	
	public String getName() 
	{
		return name;
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getWeight()
	{
		return weight;
	}
	
	public int getClassSize()
	{
		return classSize;
	}
	
	public void setMin(int _min)
	{
		min = _min;
	}
	
	public int getMin() 
	{
		return min;
	}

}
