package one.lindegaard.Core.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public abstract class AutoConfig {
	private File mFile;
	private HashMap<String, String> mCategoryComments;
	private List<String> mCategories;
	private HashMap<String, String> mCategoryNodes;

	protected AutoConfig(File file) {
		mFile = file;
		mCategories = new ArrayList<String>();
		mCategoryComments = new HashMap<String, String>();
		mCategoryNodes = new HashMap<String, String>();
	}

	protected void setCategoryComment(String category, String comment) {
		mCategoryComments.put(category, comment);
	}

	protected List<String> getCategories(String category, String[] list) {
		return mCategories;
	}

	protected List<String> getNodes(String category) {
		List<String> nodes = new ArrayList<String>();
		for (Entry<String, String> e : mCategoryNodes.entrySet()) {
			String key = e.getKey();
			if (key.equals(category)) {
				String value = e.getValue();
				nodes.add(value);
			}
		}
		return nodes;
	}

	protected void deleteNode(String category, String node) {
		for (Entry<String, String> e : mCategoryNodes.entrySet()) {
			String key = e.getKey();
			if (key.equals(category)) {
				String value = e.getValue();
				if (value.equals(node)) {
					mCategoryNodes.remove(value);
				}
			}
		}
	}

	protected void onPostLoad() throws InvalidConfigurationException {
	};

	protected void onPreSave() {
	};

	@SuppressWarnings("unchecked")
	public boolean loadConfig() {
		YamlConfiguration yml = new YamlConfiguration();
		try {
			// Make sure the file exists
			if (!mFile.exists()) {
				mFile.getParentFile().mkdirs();
				mFile.createNewFile();
			}

			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(mFile),
					StandardCharsets.UTF_8);

			// Parse the config
			yml = YamlConfiguration.loadConfiguration(inputStreamReader);
			for (Field field : getClass().getDeclaredFields()) {
				ConfigField configField = field.getAnnotation(ConfigField.class);
				if (configField == null)
					continue;

				String optionName = configField.name();
				if (optionName.isEmpty())
					optionName = field.getName();
				if (!mCategories.contains(configField.category())) {
					mCategories.add(configField.category());
				}

				field.setAccessible(true);

				String path = (configField.category().isEmpty() ? "" : configField.category() + ".") + optionName;

				mCategoryNodes.put(path, optionName);

				if (!yml.contains(path)) {
					if (field.get(this) == null)
						throw new InvalidConfigurationException(
								path + " is required to be set! Info:\n" + configField.comment());
				} else {
					// Parse the value
					if (field.getType().isArray()) {
						// Integer
						if (field.getType().getComponentType().equals(Integer.TYPE))
							field.set(this, yml.getIntegerList(path).toArray(new Integer[0]));

						// Float
						else if (field.getType().getComponentType().equals(Float.TYPE))
							field.set(this, yml.getFloatList(path).toArray(new Float[0]));

						// Double
						else if (field.getType().getComponentType().equals(Double.TYPE))
							field.set(this, yml.getDoubleList(path).toArray(new Double[0]));

						// Long
						else if (field.getType().getComponentType().equals(Long.TYPE))
							field.set(this, yml.getLongList(path).toArray(new Long[0]));

						// Short
						else if (field.getType().getComponentType().equals(Short.TYPE))
							field.set(this, yml.getShortList(path).toArray(new Short[0]));

						// Boolean
						else if (field.getType().getComponentType().equals(Boolean.TYPE))
							field.set(this, yml.getBooleanList(path).toArray(new Boolean[0]));

						// String
						else if (field.getType().getComponentType().equals(String.class)) {
							field.set(this, yml.getStringList(path).toArray(new String[0]));
						}

						// HashMap
						else if (field.getType().getComponentType().equals(HashMap.class)) {
							field.set(this, (List<HashMap<String, String>>) yml.getList(path));

						}

						// LinkedHashMap
						else if (field.getType().getComponentType().equals(LinkedHashMap.class)) {
							field.set(this, yml.getConfigurationSection(path).getValues(true));
						}

						else
							throw new IllegalArgumentException("LoadConfig - Cannot use type "
									+ field.getType().getSimpleName() + " for AutoConfiguration (Is it an Array?)");

					} else if (field.getType().equals(List.class)) {
						// List of HashMaps
						field.set(this, yml.getList(path));

					} else {
						// Integer
						if (field.getType().equals(Integer.TYPE))
							field.setInt(this, yml.getInt(path));

						// Float
						else if (field.getType().equals(Float.TYPE))
							field.setFloat(this, (float) yml.getDouble(path));

						// Double
						else if (field.getType().equals(Double.TYPE))
							field.setDouble(this, yml.getDouble(path));

						// Long
						else if (field.getType().equals(Long.TYPE))
							field.setLong(this, yml.getLong(path));

						// Short
						else if (field.getType().equals(Short.TYPE))
							field.setShort(this, (short) yml.getInt(path));

						// Boolean
						else if (field.getType().equals(Boolean.TYPE))
							field.setBoolean(this, yml.getBoolean(path));

						// ItemStack
						else if (field.getType().equals(ItemStack.class))
							field.set(this, yml.getItemStack(path));

						// String
						else if (field.getType().equals(String.class))
							field.set(this, yml.getString(path));

						// HashMap
						else if (field.getType().equals(HashMap.class)) {
							field.set(this, yml.getConfigurationSection(path).getValues(true));
						}

						// LinkedHashMap
						else if (field.getType().equals(LinkedHashMap.class)) {
							field.set(this, yml.getConfigurationSection(path).getValues(true));
						}

						else
							throw new IllegalArgumentException("LoadConfig - Cannot use type "
									+ field.getType().getSimpleName() + " for AutoConfiguration");
					}
				}
			}

			onPostLoad();

			return true;
		} catch (IOException | InvalidConfigurationException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean saveConfig() {
		try {
			
			onPreSave();

			YamlConfiguration config = new YamlConfiguration();
			Map<String, String> comments = new HashMap<String, String>();

			// Add all the category comments
			comments.putAll(mCategoryComments);

			// Add all the values
			for (Field field : getClass().getDeclaredFields()) {
				ConfigField configField = field.getAnnotation(ConfigField.class);
				if (configField == null)
					continue;

				String optionName = configField.name();
				if (optionName.isEmpty())
					optionName = field.getName();

				field.setAccessible(true);

				String path = (configField.category().isEmpty() ? "" : configField.category() + ".") + optionName;

				// Ensure the section exists
				if (!configField.category().isEmpty() && !config.contains(configField.category()))
					config.createSection(configField.category());

				// List!
				if (field.getType().equals(List.class)) {
					if (field.getName().equalsIgnoreCase("disable_achievements_in_worlds")
							|| field.getName().equalsIgnoreCase("disable_grinding_detection_in_worlds")
							|| field.getName().equalsIgnoreCase("disabled_in_worlds")) {
						config.set(path, Arrays.asList(field.get(this)));
					} else {
						List<HashMap<String, String>> commands = (List<HashMap<String, String>>) field.get(this);
						Iterator<HashMap<String, String>> itr = commands.iterator();
						while (itr.hasNext()) {
							HashMap<String, String> cmd = itr.next();
							if (cmd.get("message") == null || cmd.get("message").isEmpty())
								cmd.remove("message");
							else if (cmd.get("permission") == null || cmd.get("permission").isEmpty())
								cmd.remove("permission");

						}
						config.set(path, commands);
					}

				} else

				if (field.getType().isArray()) {
					// Integer
					if (field.getType().getComponentType().equals(Integer.TYPE))
						// TODO: Check if field is "empty"
						config.set(path, Arrays.asList((Integer[]) field.get(this)));

					// Float
					else if (field.getType().getComponentType().equals(Float.TYPE))
						config.set(path, Arrays.asList((Float[]) field.get(this)));

					// Double
					else if (field.getType().getComponentType().equals(Double.TYPE))
						config.set(path, Arrays.asList((Double[]) field.get(this)));

					// Long
					else if (field.getType().getComponentType().equals(Long.TYPE))
						config.set(path, Arrays.asList((Long[]) field.get(this)));

					// Short
					else if (field.getType().getComponentType().equals(Short.TYPE))
						config.set(path, Arrays.asList((Short[]) field.get(this)));

					// Boolean
					else if (field.getType().getComponentType().equals(Boolean.TYPE))
						config.set(path, Arrays.asList((Boolean[]) field.get(this)));

					// String
					else if (field.getType().getComponentType().equals(String.class)) {
						config.set(path, Arrays.asList((String[]) field.get(this)));
					} 
					
					// HashMap
					else if (field.getType().getComponentType().equals(HashMap.class)) {
						HashMap<String, String> map = new HashMap<String, String>();
						map = (HashMap<String, String>) field.get(this);
						config.createSection(path, map);
					}
					
					// LinkedHashMap
					else if (field.getType().getComponentType().equals(LinkedHashMap.class)) {
							config.createSection(path, (Map<String, String>) Arrays.asList(field.get(this)));
					} 
						
					else
						throw new IllegalArgumentException("SaveConfig Array - Cannot use type "
								+ field.getType().getSimpleName() + " for AutoConfiguration (is it an Array)");

				}

				else {
					// Integer
					if (field.getType().equals(Integer.TYPE))
						config.set(path, field.get(this));

					// Float
					else if (field.getType().equals(Float.TYPE))
						config.set(path, field.get(this));

					// Double
					else if (field.getType().equals(Double.TYPE))
						config.set(path, field.get(this));

					// Long
					else if (field.getType().equals(Long.TYPE))
						config.set(path, field.get(this));

					// Short
					else if (field.getType().equals(Short.TYPE))
						config.set(path, field.get(this));

					// Boolean
					else if (field.getType().equals(Boolean.TYPE))
						config.set(path, field.get(this));

					// ItemStack
					else if (field.getType().equals(ItemStack.class))
						config.set(path, field.get(this));

					// String
					else if (field.getType().equals(String.class)) {
						config.set(path, field.get(this));
					}
					// HashMap
					else if (field.getType().equals(HashMap.class)) {
						config.createSection(path, (HashMap<String, HashMap<String, String>>) field.get(this));
					} 
					
					// LinkedHashMap
					else if (field.getType().equals(LinkedHashMap.class)) {
						config.createSection(path, (LinkedHashMap<String, String>) field.get(this));
					} 
					
					else {
						throw new IllegalArgumentException("SaveConfig - Cannot use type "
								+ field.getType().getSimpleName() + " for AutoConfiguration (" + field.getName() + ")");
					}
				}

				// Record the comment
				if (!configField.comment().isEmpty())
					comments.put(path, configField.comment());
			}

			String output = config.saveToString();
			
			// Apply comments
			String category = "";
			String path = "";
			List<String> lines = new ArrayList<String>(Arrays.asList(output.split("\n")));
			int lvl = 0;
			for (int l = 0; l < lines.size(); l++) {
				String line = lines.get(l);

				if (line.startsWith("#"))
					continue;

				if (line.trim().startsWith("-"))
					continue;

				if (!line.contains(":"))
					continue;

				line = line.substring(0, line.indexOf(":"));
				lvl = getLevel(line);
				category = getCategory(path, lvl);

				if (lvl == 0)
					path = line.trim();
				else
					path = category + "." + line.trim();

				if (comments.containsKey(path)) {

					String indent = "";
					for (int i = 0; i < line.length(); i++) {
						if (line.charAt(i) == ' ')
							indent += " ";
						else
							break;
					}

					// Add in the comment lines
					String[] commentLines = comments.get(path).split("\n");
					lines.add(l++, "");
					for (int i = 0; i < commentLines.length; i++) {
						commentLines[i] = indent + "# " + commentLines[i];
						lines.add(l++, commentLines[i]);
					}
				}
			}
			
			output = "";
			for (String line : lines)
				output += line + "\n";
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(mFile), StandardCharsets.UTF_8);
			writer.write(output);
			writer.close();
			
			return true;
		}catch(

	IllegalArgumentException e)
	{
		e.printStackTrace();
	}catch(
	IllegalAccessException e)
	{
		e.printStackTrace();
	}catch(
	IOException e)
	{
			e.printStackTrace();
		}return false;
	}

	private int getLevel(String line) {
		int n = 0;
		while (line.startsWith("  ")) {
			line = line.substring(2);
			n++;
		}
		return n;
	}

	private String getCategory(String path, int lvl) {
		String[] elements = path.split("\\.");
		String result = "";
		for (int i = 0; i < lvl; i++) {
			if (result.equals(""))
				result = elements[i];
			else
				result = result + "." + elements[i];
		}
		return result;
	}
}
