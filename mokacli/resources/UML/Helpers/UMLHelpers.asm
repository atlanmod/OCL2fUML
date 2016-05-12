<?xml version = '1.0' encoding = 'ISO-8859-1' ?>
<asm version="1.0" name="0">
	<cp>
		<constant value="UMLHelpers"/>
		<constant value="main"/>
		<constant value="A"/>
		<constant value="Region"/>
		<constant value="UML"/>
		<constant value="initialPseudoState"/>
		<constant value="__initinitialPseudoState"/>
		<constant value="J.registerHelperAttribute(SS):V"/>
		<constant value="initialState"/>
		<constant value="__initinitialState"/>
		<constant value="String"/>
		<constant value="#native"/>
		<constant value="element"/>
		<constant value="__initelement"/>
		<constant value="elements"/>
		<constant value="__initelements"/>
		<constant value="6:16-6:26"/>
		<constant value="13:16-13:26"/>
		<constant value="18:16-18:22"/>
		<constant value="24:16-24:22"/>
		<constant value="self"/>
		<constant value="MUML!Region;"/>
		<constant value="Sequence"/>
		<constant value="0"/>
		<constant value="subvertex"/>
		<constant value="1"/>
		<constant value="Pseudostate"/>
		<constant value="J.oclIsKindOf(J):J"/>
		<constant value="B.not():B"/>
		<constant value="19"/>
		<constant value="CJ.including(J):CJ"/>
		<constant value="kind"/>
		<constant value="EnumLiteral"/>
		<constant value="initial"/>
		<constant value="name"/>
		<constant value="J.=(J):J"/>
		<constant value="35"/>
		<constant value="CJ.asSequence():QJ"/>
		<constant value="QJ.first():J"/>
		<constant value="7:2-7:6"/>
		<constant value="7:2-7:16"/>
		<constant value="8:3-8:4"/>
		<constant value="8:17-8:32"/>
		<constant value="8:3-8:33"/>
		<constant value="7:2-9:3"/>
		<constant value="10:3-10:4"/>
		<constant value="10:3-10:9"/>
		<constant value="10:12-10:20"/>
		<constant value="10:3-10:20"/>
		<constant value="7:2-11:3"/>
		<constant value="e"/>
		<constant value="outgoing"/>
		<constant value="J.first():J"/>
		<constant value="target"/>
		<constant value="15:2-15:6"/>
		<constant value="15:2-15:25"/>
		<constant value="15:2-15:34"/>
		<constant value="15:2-15:43"/>
		<constant value="15:2-15:50"/>
		<constant value="S"/>
		<constant value="22:2-22:6"/>
		<constant value="22:2-22:15"/>
		<constant value="22:2-22:24"/>
		<constant value="NamedElement"/>
		<constant value="IN"/>
		<constant value="J.allInstancesFrom(J):J"/>
		<constant value="18"/>
		<constant value="25:2-25:18"/>
		<constant value="25:36-25:40"/>
		<constant value="25:2-25:41"/>
		<constant value="26:3-26:4"/>
		<constant value="26:3-26:9"/>
		<constant value="26:12-26:16"/>
		<constant value="26:3-26:16"/>
		<constant value="25:2-27:3"/>
	</cp>
	<operation name="1">
		<context type="2"/>
		<parameters>
		</parameters>
		<code>
			<push arg="3"/>
			<push arg="4"/>
			<findme/>
			<push arg="5"/>
			<push arg="6"/>
			<pcall arg="7"/>
			<push arg="3"/>
			<push arg="4"/>
			<findme/>
			<push arg="8"/>
			<push arg="9"/>
			<pcall arg="7"/>
			<push arg="10"/>
			<push arg="11"/>
			<findme/>
			<push arg="12"/>
			<push arg="13"/>
			<pcall arg="7"/>
			<push arg="10"/>
			<push arg="11"/>
			<findme/>
			<push arg="14"/>
			<push arg="15"/>
			<pcall arg="7"/>
		</code>
		<linenumbertable>
			<lne id="16" begin="0" end="2"/>
			<lne id="17" begin="6" end="8"/>
			<lne id="18" begin="12" end="14"/>
			<lne id="19" begin="18" end="20"/>
		</linenumbertable>
		<localvariabletable>
			<lve slot="0" name="20" begin="0" end="23"/>
		</localvariabletable>
	</operation>
	<operation name="6">
		<context type="21"/>
		<parameters>
		</parameters>
		<code>
			<push arg="22"/>
			<push arg="11"/>
			<new/>
			<push arg="22"/>
			<push arg="11"/>
			<new/>
			<load arg="23"/>
			<get arg="24"/>
			<iterate/>
			<store arg="25"/>
			<load arg="25"/>
			<push arg="26"/>
			<push arg="4"/>
			<findme/>
			<call arg="27"/>
			<call arg="28"/>
			<if arg="29"/>
			<load arg="25"/>
			<call arg="30"/>
			<enditerate/>
			<iterate/>
			<store arg="25"/>
			<load arg="25"/>
			<get arg="31"/>
			<push arg="32"/>
			<push arg="11"/>
			<new/>
			<dup/>
			<push arg="33"/>
			<set arg="34"/>
			<call arg="35"/>
			<call arg="28"/>
			<if arg="36"/>
			<load arg="25"/>
			<call arg="30"/>
			<enditerate/>
			<call arg="37"/>
			<call arg="38"/>
		</code>
		<linenumbertable>
			<lne id="39" begin="6" end="6"/>
			<lne id="40" begin="6" end="7"/>
			<lne id="41" begin="10" end="10"/>
			<lne id="42" begin="11" end="13"/>
			<lne id="43" begin="10" end="14"/>
			<lne id="44" begin="3" end="19"/>
			<lne id="45" begin="22" end="22"/>
			<lne id="46" begin="22" end="23"/>
			<lne id="47" begin="24" end="29"/>
			<lne id="48" begin="22" end="30"/>
			<lne id="49" begin="0" end="37"/>
		</linenumbertable>
		<localvariabletable>
			<lve slot="1" name="50" begin="9" end="18"/>
			<lve slot="1" name="50" begin="21" end="34"/>
			<lve slot="0" name="20" begin="0" end="37"/>
		</localvariabletable>
	</operation>
	<operation name="9">
		<context type="21"/>
		<parameters>
		</parameters>
		<code>
			<load arg="23"/>
			<get arg="5"/>
			<get arg="51"/>
			<call arg="52"/>
			<get arg="53"/>
		</code>
		<linenumbertable>
			<lne id="54" begin="0" end="0"/>
			<lne id="55" begin="0" end="1"/>
			<lne id="56" begin="0" end="2"/>
			<lne id="57" begin="0" end="3"/>
			<lne id="58" begin="0" end="4"/>
		</linenumbertable>
		<localvariabletable>
			<lve slot="0" name="20" begin="0" end="4"/>
		</localvariabletable>
	</operation>
	<operation name="13">
		<context type="59"/>
		<parameters>
		</parameters>
		<code>
			<load arg="23"/>
			<get arg="14"/>
			<call arg="52"/>
		</code>
		<linenumbertable>
			<lne id="60" begin="0" end="0"/>
			<lne id="61" begin="0" end="1"/>
			<lne id="62" begin="0" end="2"/>
		</linenumbertable>
		<localvariabletable>
			<lve slot="0" name="20" begin="0" end="2"/>
		</localvariabletable>
	</operation>
	<operation name="15">
		<context type="59"/>
		<parameters>
		</parameters>
		<code>
			<push arg="22"/>
			<push arg="11"/>
			<new/>
			<push arg="63"/>
			<push arg="4"/>
			<findme/>
			<push arg="64"/>
			<call arg="65"/>
			<iterate/>
			<store arg="25"/>
			<load arg="25"/>
			<get arg="34"/>
			<load arg="23"/>
			<call arg="35"/>
			<call arg="28"/>
			<if arg="66"/>
			<load arg="25"/>
			<call arg="30"/>
			<enditerate/>
		</code>
		<linenumbertable>
			<lne id="67" begin="3" end="5"/>
			<lne id="68" begin="6" end="6"/>
			<lne id="69" begin="3" end="7"/>
			<lne id="70" begin="10" end="10"/>
			<lne id="71" begin="10" end="11"/>
			<lne id="72" begin="12" end="12"/>
			<lne id="73" begin="10" end="13"/>
			<lne id="74" begin="0" end="18"/>
		</linenumbertable>
		<localvariabletable>
			<lve slot="1" name="50" begin="9" end="17"/>
			<lve slot="0" name="20" begin="0" end="18"/>
		</localvariabletable>
	</operation>
</asm>
