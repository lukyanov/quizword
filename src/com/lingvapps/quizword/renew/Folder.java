package com.lingvapps.quizword.renew;

public class Folder {
    
	public static final int FOLDER_TYPE_FOLDER = 0;
	public static final int FOLDER_TYPE_CLASS = 1;
	
    private String  name;
    private Integer id;
	private Integer type;
    
    public Folder(Integer type, Integer id, String name) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
    
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

	public Integer getType() {
		return type;
	}
}
