package translation;

public class attributeDefinition 
{
	public String code;
}

class IDENT
{
	public String name;
	
	public IDENT(String name)
	{
		this.name = name;
	}
}

class type_spec extends attributeDefinition
{
	public String type;
	public int width;
	
	public type_spec(String type,int width)
	{
		super();
		this.type = type;
		this.width = width;
	}
}

class int_literal extends attributeDefinition
{
	public int lexval;
	
	public int_literal(int lexval)
	{
		super();
		this.lexval = lexval;
	}
}

class expr extends attributeDefinition
{
	public String place;
	public String True;
	public String False;
	
	public expr(String place)
	{
		super();
		this.place = place;
	}
	
	public expr(String True,String False)
	{
		super();
		this.True = True;
		this.False = False;
	}
}

class if_stmt extends attributeDefinition
{
	public String next;
	
	public if_stmt(String next)
	{
		super();
		this.next = next;
	}
}

class while_stmt extends attributeDefinition
{
	public String begin;
	public String next;
	
	public while_stmt(String begin,String next)
	{
		this.begin = begin;
		this.next = next;
	}
}


