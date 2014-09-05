package cnpat.test.bean;

import java.util.List;

/**
 * 过程
 * @author ThinkMan
 *
 */
public class PmpProcess {
	private Integer id;
	private String name;
	private List<InOutput> input;
	private List<InOutput> output;
	private List<Tool> tool;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<InOutput> getInput() {
		return input;
	}
	public void setInput(List<InOutput> input) {
		this.input = input;
	}
	public List<InOutput> getOutput() {
		return output;
	}
	public void setOutput(List<InOutput> output) {
		this.output = output;
	}
	public List<Tool> getTool() {
		return tool;
	}
	public void setTool(List<Tool> tool) {
		this.tool = tool;
	} 
}
