# Spin Treats

Spin Treats is a mobile 2D game developed with the LibGDX framework.

> LibGDX is a relatively low level, free, open source cross platform game development framework. The goal of the project is to assist [developers] in creating games/applications and deploy to desktop and mobile platforms.

## Installation

To run the game on your computer you need to import the project into an IDE (Intellij/Android Studio, Eclipse, Neatbeans).

This [link](https://libgdx.badlogicgames.com/documentation/gettingstarted/Importing%20into%20IDE.html) will help you with that.


## [WIP] Usage
The specifications for all the levels of the game are stored in the **planetas_data.xml** (android/assets/planetas_data.xml) file. That is: the position of each planet, the position of each treat, the quantity of lives per level, the speed and direction of each planet's rotation, etc.

The whole game is divided by **sections**, each section is formed by several **levels** and each level is formed by one or more **challenges**. In order to surpass a level all of its challenges must be completed.

The XML file specifies this whole structure with tags and attributes. Here is an example: 

```xml
<challenge time="15">
  <planet1 x="0.5" y="0.75" size="0.32" speed="0.8"/>
  <planet1 x="0.5" y="0.25" size="0.32" speed="0.8"/>
  <treat x="0.5" y="0.4"/>
  <treat x="0.5" y="0.5"/>
  <treat x="0.5" y="0.6"/>
</challenge>
```

This challenge has a time limit of 15 seconds as you can see by the **time** attribute of the **<challenge>** element. 

It has two planets of type 1 (**<planet1/>**), their position, size and speed is indicated by the **x**, **y**, **size** and **speed** attributes.

Every challenge must have at least one treat as the objective of the challenge is to collect them all. This challenge has three treats (**<treat/>**), their position is specified by the **x** and **y** attributes.

However, this is just the specification of one challenge, the whole XML file would look something like this: 


```xml
<planetasData>
  <section>
    <level>
      <challenge time="15">
        <planet1 x="0.5" y="0.75" size="0.32" speed="0.8"/>
        <planet1 x="0.5" y="0.25" size="0.32" speed="0.8"/>
        <treat x="0.5" y="0.4"/>
        <treat x="0.5" y="0.5"/>
        <treat x="0.5" y="0.6"/>
      </challenge>
      <!--
      <challenge>...</challenge>
      <challenge>...</challenge>
      -->
    </level>
    <!--
    <level>...</level>
    <level>...</level>
    <level>...</level>
    -->
  </section>
  <!--
  <section>...</section>
  <section>...</section>
  <section>...</section>
  -->
</planetasData>
```

#### Surrounding planets with treats
Although it is possible to set the static/specific position of each treat (as seen in the previous example), this functionality is actually not used often through the current specification of the game because it would be very unpractical to calculate and specify each treat's position through its x and y coordinates. 

Instead, there is a more convenient way of specifying treat's positions. That is, to link the position of a treat with the position of a planet (which would have a static position). The way to do this is surrounding planets with treats. Here is an example:

```xml
<challenge time="15">
  <planet1 x="0.28" y="0.74" size="0.30" speed="0.8"/>
  <treat>
    <planet1 x="0.5" y="0.5" size="0.30" speed="0.8"/>
  </treat>
  <treat>
    <planet1 x="0.72" y="0.26" size="0.30" speed="0.8"/>
  </treat>
</challenge>
```

Here, instead of specifying the position of each treat, we're stating that we want the second and third planet to be surrounded with treats. The game will use the position of the planet tag that the treat is surrounding and use it to calculate the position of several treats and generate them dynamically.

If we modify the size or position of a surrounded planet its treats will also change their position. This is a less messy way of specifying the treats of a challenge.

Depending of the planet size, the game will choose a quantity of treats that fit the best its perimeter. Instead, if you want the specify the quantity of treats you can do it through the **quantity** attribute. Also, you can choose the **angle** (by default 360) that the surrounding treats will ocupy from the planet and the **degree** (by default 0) to which it should start generating the treats. Here is an example: 

```xml
<challenge time="15">
  <planet1 x="0.28" y="0.74" size="0.30" speed="0.8"/>
  <treat quantity="10" angle="360" degree="0">
    <planet1 x="0.5" y="0.5" size="0.30" speed="0.8"/>
    <planet1 x="0.72" y="0.26" size="0.30" speed="0.8"/>
  </treat>
</challenge>
```

This challenge has two planets surrounded with treats. We specify explicitely that we want each one to be surrounded with 10 treats and that we want an angle of 360 and the degree is 0 (default values). Also, we can see in this example that **a treat element can surround more that one planet** and the game will generate treats for all surrounded planets.

Here is another example: 

```xml
<challenge time="15">
  <planet1 x="0.28" y="0.74" size="0.30" speed="0.8"/>
  <treat quantity="5" angle="180" degree="90">
    <planet1 x="0.5" y="0.5" size="0.30" speed="0.8"/>
  </treat>
  <treat quantity="1" degree="90">
    <planet1 x="0.72" y="0.26" size="0.30" speed="0.8"/>
  </treat>
</challenge>
```

In this challenge one of the planets is surrounded by 5 treats (quantity="5") but just half (angle="180") of its perimeter. The surrounded fraction of the perimeter is the "left" one because it starts generating the treats from 90 to 270 (90 + 180) degree. The other planet has only one treat (quantity="1")  right on top of it (degree="90").

#### Treats in line (planet to planet)

Another way of distributing treats is generating them "in line" from one planet to another one. Here is an example of how to do this: 

```xml
<challenge>
  <planet1 id="p1" x="0.3" y="0.73" size="0.33" speed="0.64"/>
  <planet1 id="p2" x="0.65" y="0.34" size="0.45" speed="0.6"/>
  <treat type="line" from="p1" to="p2"/>
</challenge>
```

In this challenge, we have only two planets, each one was assigned an id. The treat tag has a **type** attribute set to **"line"**. The **to** and **from** attributes indicate the ids of two planets that will specify the path that the "line of treats" will follow, which is generated with the position of the specified planets. 

### Another elements, tags and attributes

| Tag / Element | Attributes    |
| ------------- | ------------- |
| planet1       | id, x, y, size, habilita, direction, speed |
| planet2       | id, x, y, size, habilita, direction, speed, visits |
| planet3       | id, x, y, size, habilita, direction, speed, thornsSpeed, quantity |
| planet4       | id, x, y, size, habilita, direction |
| planet5       | id, x, y, size, habilita, direction, speed, time, to, degree |
| planet6       | id, x, y, size, habilita, direction |
| planet7       | id, x, y, size, habilita, direction, speed |
| treat         | x, y, quantity, degree, angle, collection, type, from, to, efecto | 
| treatEfecto   | x, y, degree | 
| treatHabilita | x, y, degree, time | 
| treatTiempo   | x, y, quantity, degree, angle, collection, type, from, to, efecto |
| level         | lives |
| challenge     | time, border |
| extra         | | 
| section       | |
| advice        | imagePath |
