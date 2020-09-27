# DecsNPCs
I spent about 2 months searching for all of the relevant information to put together my own NPCs. I wasn't that into the idea of using an external API like Citizens because that doesn't teach me anything. So I made DecsNPCs, a lightweight and easy to understand NPC api with some simple features.

You should really use Citizens if you need to do any hardcore NPC work like making them walk around and attack players or entities. While not impossible for an amateur coder like me to find and put together all of the relevant stuff to do what they do, I would feel much more comfortable myself using something that has been proven and used in all of the big servers. If it makes you feel any better about not being able to avoid Citizens then you should know that Hypixel uses Citizens (really, just login and type /citizens).

Thanks in advance for considering to use this. This is the first project I think is worthy of public release after only 4 months of Java coding experience. Helpful criticsm and suggestions will be much appreciated.

# Features
* This API has 0 dependencies to outside plugins!
* Simple NPC creation and manipulation
  * Have full control of where NPCs spawn and which direction they face
  * Ability to control which players can see which NPCs
  * Detect when players interact with NPCs (RightClickNPCEvent)
* Enable or disable NPCs looking at players within 5 blocks of them

# Getting started
The following repository allows any github repository to be used as a dependency in a maven project:
```
<repository>
  <id>jitpack.io</id>
		<url>https://jitpack.io</url>
</repository>
  ```
  And then the following dependency:
  ```
<dependency>
  <groupId>com.github.dec4234</groupId>
	 <artifactId>DecsNPCs</artifactId>
	 <version>master</version>
</dependency>
 ```
