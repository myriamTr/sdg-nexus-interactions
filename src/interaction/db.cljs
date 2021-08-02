(ns interaction.db
  (:require [clojure.set :as set]))


(def sdgs->targets
  {1 {:targets 7}
   2 {:targets 8}
   3 {:targets 13}
   4 {:targets 10}
   5 {:targets 9}
   6 {:targets 8}
   7 {:targets 5}
   8 {:targets 11}
   9 {:targets 8}
   10 {:targets 10}
   11 {:targets 10}
   12 {:targets 11}
   13 {:targets 5}
   14 {:targets 12}
   15 {:targets 12}
   16 {:targets 11}
   17 {:targets 19}})

(def sdgs-targets->label
  {"1.6" "1.a"
   "1.7" "1.b"
   "2.6" "2.a"
   "2.7" "2.b"
   "2.8" "2.c"
   "3.10" "3.a"
   "3.11" "3.b"
   "3.12" "3.c"
   "3.13" "3.d"
   "4.8"  "4.a"
   "4.9"  "4.b"
   "4.10" "4.c"
   "5.7"  "5.a"
   "5.8"  "5.b"
   "5.9"  "5.c"
   "6.7"  "6.a"
   "6.8"  "6.b"
   "7.4"  "7.a"
   "7.5"  "7.b"
   "8.10" "8.a"
   "8.11" "8.b"
   "9.6"  "9.a"
   "9.7"  "9.b"
   "9.8"  "9.c"
   "10.8" "10.a"
   "10.9" "10.b"
   "10.10" "10.c"
   "11.8" "11.a"
   "11.9" "11.b"
   "11.10" "11.c"
   "12.9" "12.a"
   "12.10" "12.b"
   "12.11" "12.c"
   "13.4" "13.a"
   "13.5" "13.b"
   "14.10" "14.a"
   "14.11" "14.b"
   "14.12" "14.c"
   "15.10" "15.a"
   "15.11" "15.b"
   "15.12" "15.c"
   "16.10" "16.a"
   "16.11" "16.b"})

(def sdg+target-label->targets-numerical (set/map-invert sdgs-targets->label))

(def id->title
  {"1" "1: End poverty in all its forms everywhere",
 "1.1"
 "1.1: By 2030, eradicate extreme poverty for all people everywhere, currently measured as people living on less than $1.25 a day",
 "1.2"
 "1.2: By 2030, reduce at least by half the proportion of men, women and children of all ages living in poverty in all its dimensions according to national definitions",
 "1.3"
 "1.3: Implement nationally appropriate social protection systems and measures for all, including floors, and by 2030 achieve substantial coverage of the poor and the vulnerable",
 "1.4"
 "1.4: By 2030, ensure that all men and women, in particular the poor and the vulnerable, have equal rights to economic resources, as well as access to basic services, ownership and control over land and other forms of property, inheritance, natural resources, appropriate new technology and financial services, including microfinance",
 "1.5"
 "1.5: By 2030, build the resilience of the poor and those in vulnerable situations and reduce their exposure and vulnerability to climate-related extreme events and other economic, social and environmental shocks and disasters",
 "1.a"
 "1.a: Ensure significant mobilization of resources from a variety of sources, including through enhanced development cooperation, in order to provide adequate and predictable means for developing countries, in particular least developed countries, to implement programmes and policies to end poverty in all its dimensions",
 "1.b"
 "1.b: Create sound policy frameworks at the national, regional and international levels, based on pro-poor and gender-sensitive development strategies, to support accelerated investment in poverty eradication actions",
 "10" "10: Reduce inequality within and among countries",
 "10.1"
 "10.1: By 2030, progressively achieve and sustain income growth of the bottom 40 per cent of the population at a rate higher than the national average",
 "10.2"
 "10.2: By 2030, empower and promote the social, economic and political inclusion of all, irrespective of age, sex, disability, race, enilthnicity, origin, religion or economic or other status",
 "10.3"
 "10.3: Ensure equal opportunity and reduce inequalities of outcome, including by eliminating discriminatory laws, policies and practices and promoting appropriate legislation, policies and action in this regard",
 "10.4"
 "10.4: Adopt policies, especially fiscal, wage and social protection policies, and progressively achieve greater equality",
 "10.5"
 "10.5: Improve the regulation and monitoring of global financial markets and institutions and strengthen the implementation of such regulations",
 "10.6"
 "10.6: Ensure enhanced representation and voice for developing countries in decision-making in global international economic and financial institutions in order to deliver more effective, credible, accountable and legitimate institutions",
 "10.7"
 "10.7: Facilitate orderly, safe, regular and responsible migration and mobility of people, including through the implementation of planned and well-managed migration policies",
 "10.a"
 "10.a: Implement the principle of special and differential treatment for developing countries, in particular least developed countries, in accordance with World Trade Organization agreements",
 "10.b"
 "10.b: Encourage official development assistance and financial flows, including foreign direct investment, to States where the need is greatest, in particular least developed countries, African countries, small island developing States and landlocked developing countries, in accordance with their national plans and programmes",
 "10.c"
 "10.c: By 2030, reduce to less than 3 per cent the transaction costs of migrant remittances and eliminate remittance corridors with costs higher than 5 per cent",
 "11"
 "11: Make cities and human settlements inclusive, safe, resilient and sustainable",
 "11.1"
 "11.1: By 2030, ensure access for all to adequate, safe and affordable housing and basic services and upgrade slums",
 "11.2"
 "11.2: By 2030, provide access to safe, affordable, accessible and sustainable transport systems for all, improving road safety, notably by expanding public transport, with special attention to the needs of those in vulnerable situations, women, children, persons with disabilities and older persons",
 "11.3"
 "11.3: By 2030, enhance inclusive and sustainable urbanization and capacity for participatory, integrated and sustainable human settlement planning and management in all countries",
 "11.4"
 "11.4: Strengthen efforts to protect and safeguard the world’s cultural and natural heritage",
 "11.5"
 "11.5: By 2030, significantly reduce the number of deaths and the number of people affected and substantially decrease the direct economic losses relative to global gross domestic product caused by disasters, including water-related disasters, with a focus on protecting the poor and people in vulnerable situations",
 "11.6"
 "11.6: By 2030, reduce the adverse per capita environmental impact of cities, including by paying special attention to air quality and municipal and other waste management",
 "11.7"
 "11.7: By 2030, provide universal access to safe, inclusive and accessible, green and public spaces, in particular for women and children, older persons and persons with disabilities",
 "11.a"
 "11.a: Support positive economic, social and environmental links between urban, peri-urban and rural areas by strengthening national and regional development planning",
 "11.b"
 "11.b: By 2020, substantially increase the number of cities and human settlements adopting and implementing integrated policies and plans towards inclusion, resource efficiency, mitigation and adaptation to climate change, resilience to disasters, and develop and implement, in line with the Sendai Framework for Disaster Risk Reduction 2015–2030, holistic disaster risk management at all levels",
 "11.c"
 "11.c: Support least developed countries, including through financial and technical assistance, in building sustainable and resilient buildings utilizing local materials",
 "12" "12: Ensure sustainable consumption and production patterns",
 "12.1"
 "12.1: Implement the 10‑Year Framework of Programmes on Sustainable Consumption and Production Patterns, all countries taking action, with developed countries taking the lead, taking into account the development and capabilities of developing countries",
 "12.2"
 "12.2: By 2030, achieve the sustainable management and efficient use of natural resources",
 "12.3"
 "12.3: By 2030, halve per capita global food waste at the retail and consumer levels and reduce food losses along production and supply chains, including post-harvest losses",
 "12.4"
 "12.4: By 2020, achieve the environmentally sound management of chemicals and all wastes throughout their life cycle, in accordance with agreed international frameworks, and significantly reduce their release to air, water and soil in order to minimize their adverse impacts on human health and the environment",
 "12.5"
 "12.5: By 2030, substantially reduce waste generation through prevention, reduction, recycling and reuse",
 "12.6"
 "12.6: Encourage companies, especially large and transnational companies, to adopt sustainable practices and to integrate sustainability information into their reporting cycle",
 "12.7"
 "12.7: Promote public procurement practices that are sustainable, in accordance with national policies and priorities",
 "12.8"
 "12.8: By 2030, ensure that people everywhere have the relevant information and awareness for sustainable development and lifestyles in harmony with nature",
 "12.a"
 "12.a: Support developing countries to strengthen their scientific and technological capacity to move towards more sustainable patterns of consumption and production",
 "12.b"
 "12.b: Develop and implement tools to monitor sustainable development impacts for sustainable tourism that creates jobs and promotes local culture and products",
 "12.c"
 "12.c: Rationalize inefficient fossil-fuel subsidies that encourage wasteful consumption by removing market distortions, in accordance with national circumstances, including by restructuring taxation and phasing out those harmful subsidies, where they exist, to reflect their environmental impacts, taking fully into account the specific needs and conditions of developing countries and minimizing the possible adverse impacts on their development in a manner that protects the poor and the affected communities",
 "13"
 "13: Take urgent action to combat climate change and its impacts",
 "13.1"
 "13.1: Strengthen resilience and adaptive capacity to climate-related hazards and natural disasters in all countries",
 "13.2"
 "13.2: Integrate climate change measures into national policies, strategies and planning",
 "13.3"
 "13.3: Improve education, awareness-raising and human and institutional capacity on climate change mitigation, adaptation, impact reduction and early warning",
 "13.a"
 "13.a: Implement the commitment undertaken by developed-country parties to the United Nations Framework Convention on Climate Change to a goal of mobilizing jointly $100 billion annually by 2020 from all sources to address the needs of developing countries in the context of meaningful mitigation actions and transparency on implementation and fully operationalize the Green Climate Fund through its capitalization as soon as possible",
 "13.b"
 "13.b: Promote mechanisms for raising capacity for effective climate change-related planning and management in least developed countries and small island developing States, including focusing on women, youth and local and marginalized communities",
 "14"
 "14: Conserve and sustainably use the oceans, seas and marine resources for sustainable development",
 "14.1"
 "14.1: By 2025, prevent and significantly reduce marine pollution of all kinds, in particular from land-based activities, including marine debris and nutrient pollution",
 "14.2"
 "14.2: By 2020, sustainably manage and protect marine and coastal ecosystems to avoid significant adverse impacts, including by strengthening their resilience, and take action for their restoration in order to achieve healthy and productive oceans",
 "14.3"
 "14.3: Minimize and address the impacts of ocean acidification, including through enhanced scientific cooperation at all levels",
 "14.4"
 "14.4: By 2020, effectively regulate harvesting and end overfishing, illegal, unreported and unregulated fishing and destructive fishing practices and implement science-based management plans, in order to restore fish stocks in the shortest time feasible, at least to levels that can produce maximum sustainable yield as determined by their biological characteristics",
 "14.5"
 "14.5: By 2020, conserve at least 10 per cent of coastal and marine areas, consistent with national and international law and based on the best available scientific information",
 "14.6"
 "14.6: By 2020, prohibit certain forms of fisheries subsidies which contribute to overcapacity and overfishing, eliminate subsidies that contribute to illegal, unreported and unregulated fishing and refrain from introducing new such subsidies, recognizing that appropriate and effective special and differential treatment for developing and least developed countries should be an integral part of the World Trade Organization fisheries subsidies negotiation3",
 "14.7"
 "14.7: By 2030, increase the economic benefits to small island developing States and least developed countries from the sustainable use of marine resources, including through sustainable management of fisheries, aquaculture and tourism",
 "14.a"
 "14.a: Increase scientific knowledge, develop research capacity and transfer marine technology, taking into account the Intergovernmental Oceanographic Commission Criteria and Guidelines on the Transfer of Marine Technology, in order to improve ocean health and to enhance the contribution of marine biodiversity to the development of developing countries, in particular small island developing States and least developed countries",
 "14.b"
 "14.b: Provide access for small-scale artisanal fishers to marine resources and markets",
 "14.c"
 "14.c: Enhance the conservation and sustainable use of oceans and their resources by implementing international law as reflected in the United Nations Convention on the Law of the Sea, which provides the legal framework for the conservation and sustainable use of oceans and their resources, as recalled in paragraph 158 of “The future we want”",
 "15"
 "15: Protect, restore and promote sustainable use of terrestrial ecosystems, sustainably manage forests, combat desertification, and halt and reverse land degradation and halt biodiversity loss",
 "15.1"
 "15.1: By 2020, ensure the conservation, restoration and sustainable use of terrestrial and inland freshwater ecosystems and their services, in particular forests, wetlands, mountains and drylands, in line with obligations under international agreements",
 "15.2"
 "15.2: By 2020, promote the implementation of sustainable management of all types of forests, halt deforestation, restore degraded forests and substantially increase afforestation and reforestation globally",
 "15.3"
 "15.3: By 2030, combat desertification, restore degraded land and soil, including land affected by desertification, drought and floods, and strive to achieve a land degradation-neutral world",
 "15.4"
 "15.4: By 2030, ensure the conservation of mountain ecosystems, including their biodiversity, in order to enhance their capacity to provide benefits that are essential for sustainable development",
 "15.5"
 "15.5: Take urgent and significant action to reduce the degradation of natural habitats, halt the loss of biodiversity and, by 2020, protect and prevent the extinction of threatened species",
 "15.6"
 "15.6: Promote fair and equitable sharing of the benefits arising from the utilization of genetic resources and promote appropriate access to such resources, as internationally agreed",
 "15.7"
 "15.7: Take urgent action to end poaching and trafficking of protected species of flora and fauna and address both demand and supply of illegal wildlife products",
 "15.8"
 "15.8: By 2020, introduce measures to prevent the introduction and significantly reduce the impact of invasive alien species on land and water ecosystems and control or eradicate the priority species",
 "15.9"
 "15.9: By 2020, integrate ecosystem and biodiversity values into national and local planning, development processes, poverty reduction strategies and accounts",
 "15.a"
 "15.a: Mobilize and significantly increase financial resources from all sources to conserve and sustainably use biodiversity and ecosystems",
 "15.b"
 "15.b: Mobilize significant resources from all sources and at all levels to finance sustainable forest management and provide adequate incentives to developing countries to advance such management, including for conservation and reforestation",
 "15.c"
 "15.c: Enhance global support for efforts to combat poaching and trafficking of protected species, including by increasing the capacity of local communities to pursue sustainable livelihood opportunities",
 "16"
 "16: Promote peaceful and inclusive societies for sustainable development, provide access to justice for all and build effective, accountable and inclusive institutions at all levels",
 "16.1"
 "16.1: Significantly reduce all forms of violence and related death rates everywhere",
 "16.10"
 "16.10: Ensure public access to information and protect fundamental freedoms, in accordance with national legislation and international agreements",
 "16.2"
 "16.2: End abuse, exploitation, trafficking and all forms of violence against and torture of children",
 "16.3"
 "16.3: Promote the rule of law at the national and international levels and ensure equal access to justice for all",
 "16.4"
 "16.4: By 2030, significantly reduce illicit financial and arms flows, strengthen the recovery and return of stolen assets and combat all forms of organized crime",
 "16.5"
 "16.5: Substantially reduce corruption and bribery in all their forms",
 "16.6"
 "16.6: Develop effective, accountable and transparent institutions at all levels",
 "16.7"
 "16.7: Ensure responsive, inclusive, participatory and representative decision-making at all levels",
 "16.8"
 "16.8: Broaden and strengthen the participation of developing countries in the institutions of global governance",
 "16.9"
 "16.9: By 2030, provide legal identity for all, including birth registration",
 "16.a"
 "16.a: Strengthen relevant national institutions, including through international cooperation, for building capacity at all levels, in particular in developing countries, to prevent violence and combat terrorism and crime",
 "16.b"
 "16.b: Promote and enforce non-discriminatory laws and policies for sustainable development",
 "17"
 "17: Strengthen the means of implementation and revitalize the Global Partnership for Sustainable Development",
 "17.1"
 "17.1: Strengthen domestic resource mobilization, including through international support to developing countries, to improve domestic capacity for tax and other revenue collection",
 "17.10"
 "17.10: Promote a universal, rules-based, open, non‑discriminatory and equitable multilateral trading system under the World Trade Organization, including through the conclusion of negotiations under its Doha Development Agenda",
 "17.11"
 "17.11: Significantly increase the exports of developing countries, in particular with a view to doubling the least developed countries’ share of global exports by 2020",
 "17.12"
 "17.12: Realize timely implementation of duty-free and quota-free market access on a lasting basis for all least developed countries, consistent with World Trade Organization decisions, including by ensuring that preferential rules of origin applicable to imports from least developed countries are transparent and simple, and contribute to facilitating market access",
 "17.13"
 "17.13: Enhance global macroeconomic stability, including through policy coordination and policy coherence",
 "17.14" "17.14: Enhance policy coherence for sustainable development",
 "17.15"
 "17.15: Respect each country’s policy space and leadership to establish and implement policies for poverty eradication and sustainable development",
 "17.16"
 "17.16: Enhance the Global Partnership for Sustainable Development, complemented by multi-stakeholder partnerships that mobilize and share knowledge, expertise, technology and financial resources, to support the achievement of the Sustainable Development Goals in all countries, in particular developing countries",
 "17.17"
 "17.17: Encourage and promote effective public, public-private and civil society partnerships, building on the experience and resourcing strategies of partnerships",
 "17.18"
 "17.18: By 2020, enhance capacity-building support to developing countries, including for least developed countries and small island developing States, to increase significantly the availability of high-quality, timely and reliable data disaggregated by income, gender, age, race, ethnicity, migratory status, disability, geographic location and other characteristics relevant in national contexts",
 "17.19"
 "17.19: By 2030, build on existing initiatives to develop measurements of progress on sustainable development that complement gross domestic product, and support statistical capacity-building in developing countries",
 "17.2"
 "17.2: Developed countries to implement fully their official development assistance commitments, including the commitment by many developed countries to achieve the target of 0.7% of gross national income for official development assistance (ODA/GNI) to developing countries and 0.15 to 0.20 per cent of ODA/GNI to least developed countries; ODA providers are encouraged to consider setting a target to provide at least 0.2% of ODA/GNI to least developed countries",
 "17.3"
 "17.3: Mobilize additional financial resources for developing countries from multiple sources",
 "17.4"
 "17.4: Assist developing countries in attaining long-term debt sustainability through coordinated policies aimed at fostering debt financing, debt relief and debt restructuring, as appropriate, and address the external debt of highly indebted poor countries to reduce debt distress",
 "17.5"
 "17.5: Adopt and implement investment promotion regimes for least developed countries",
 "17.6"
 "17.6: Enhance North-South, South-South and triangular regional and international cooperation on and access to science, technology and innovation and enhance knowledge-sharing on mutually agreed terms, including through improved coordination among existing mechanisms, in particular at the United Nations level, and through a global technology facilitation mechanism",
 "17.7"
 "17.7: Promote the development, transfer, dissemination and diffusion of environmentally sound technologies to developing countries on favourable terms, including on concessional and preferential terms, as mutually agreed",
 "17.8"
 "17.8: Fully operationalize the technology bank and science, technology and innovation capacity-building mechanism for least developed countries by 2017 and enhance the use of enabling technology, in particular information and communications technology",
 "17.9"
 "17.9: Enhance international support for implementing effective and targeted capacity-building in developing countries to support national plans to implement all the Sustainable Development Goals, including through North-South, South-South and triangular cooperation",
 "2"
 "2: End hunger, achieve food security and improved nutrition and promote sustainable agriculture",
 "2.1"
 "2.1: By 2030, end hunger and ensure access by all people, in particular the poor and people in vulnerable situations, including infants, to safe, nutritious and sufficient food all year round",
 "2.2"
 "2.2: By 2030, end all forms of malnutrition, including achieving, by 2025, the internationally agreed targets on stunting and wasting in children under 5 years of age, and address the nutritional needs of adolescent girls, pregnant and lactating women and older persons",
 "2.3"
 "2.3: By 2030, double the agricultural productivity and incomes of small-scale food producers, in particular women, indigenous peoples, family farmers, pastoralists and fishers, including through secure and equal access to land, other productive resources and inputs, knowledge, financial services, markets and opportunities for value addition and non-farm employment",
 "2.4"
 "2.4: By 2030, ensure sustainable food production systems and implement resilient agricultural practices that increase productivity and production, that help maintain ecosystems, that strengthen capacity for adaptation to climate change, extreme weather, drought, flooding and other disasters and that progressively improve land and soil quality",
 "2.5"
 "2.5: By 2020, maintain the genetic diversity of seeds, cultivated plants and farmed and domesticated animals and their related wild species, including through soundly managed and diversified seed and plant banks at the national, regional and international levels, and promote access to and fair and equitable sharing of benefits arising from the utilization of genetic resources and associated traditional knowledge, as internationally agreed",
 "2.a"
 "2.a: Increase investment, including through enhanced international cooperation, in rural infrastructure, agricultural research and extension services, technology development and plant and livestock gene banks in order to enhance agricultural productive capacity in developing countries, in particular least developed countries",
 "2.b"
 "2.b: Correct and prevent trade restrictions and distortions in world agricultural markets, including through the parallel elimination of all forms of agricultural export subsidies and all export measures with equivalent effect, in accordance with the mandate of the Doha Development Round",
 "2.c"
 "2.c: Adopt measures to ensure the proper functioning of food commodity markets and their derivatives and facilitate timely access to market information, including on food reserves, in order to help limit extreme food price volatility",
 "3"
 "3: Ensure healthy lives and promote well-being for all at all ages",
 "3.1"
 "3.1: By 2030, reduce the global maternal mortality ratio to less than 70 per 100,000 live births",
 "3.2"
 "3.2: By 2030, end preventable deaths of newborns and children under 5 years of age, with all countries aiming to reduce neonatal mortality to at least as low as 12 per 1,000 live births and under‑5 mortality to at least as low as 25 per 1,000 live births",
 "3.3"
 "3.3: By 2030, end the epidemics of AIDS, tuberculosis, malaria and neglected tropical diseases and combat hepatitis, water-borne diseases and other communicable diseases",
 "3.4"
 "3.4: By 2030, reduce by one third premature mortality from non-communicable diseases through prevention and treatment and promote mental health and well-being",
 "3.5"
 "3.5: Strengthen the prevention and treatment of substance abuse, including narcotic drug abuse and harmful use of alcohol",
 "3.6"
 "3.6: By 2020, halve the number of global deaths and injuries from road traffic accidents",
 "3.7"
 "3.7: By 2030, ensure universal access to sexual and reproductive health-care services, including for family planning, information and education, and the integration of reproductive health into national strategies and programmes",
 "3.8"
 "3.8: Achieve universal health coverage, including financial risk protection, access to quality essential health-care services and access to safe, effective, quality and affordable essential medicines and vaccines for all",
 "3.9"
 "3.9: By 2030, substantially reduce the number of deaths and illnesses from hazardous chemicals and air, water and soil pollution and contamination",
 "3.a"
 "3.a: Strengthen the implementation of the World Health Organization Framework Convention on Tobacco Control in all countries, as appropriate",
 "3.b"
 "3.b: Support the research and development of vaccines and medicines for the communicable and non‑communicable diseases that primarily affect developing countries, provide access to affordable essential medicines and vaccines, in accordance with the Doha Declaration on the TRIPS Agreement and Public Health, which affirms the right of developing countries to use to the full the provisions in the Agreement on Trade-Related Aspects of Intellectual Property Rights regarding flexibilities to protect public health, and, in particular, provide access to medicines for all",
 "3.c"
 "3.c: Substantially increase health financing and the recruitment, development, training and retention of the health workforce in developing countries, especially in least developed countries and small island developing States",
 "3.d"
 "3.d: Strengthen the capacity of all countries, in particular developing countries, for early warning, risk reduction and management of national and global health risks",
 "4"
 "4: Ensure inclusive and equitable quality education and promote lifelong learning opportunities for all",
 "4.1"
 "4.1: By 2030, ensure that all girls and boys complete free, equitable and quality primary and secondary education leading to relevant and effective learning outcomes",
 "4.2"
 "4.2: By 2030, ensure that all girls and boys have access to quality early childhood development, care and pre‑primary education so that they are ready for primary education",
 "4.3"
 "4.3: By 2030, ensure equal access for all women and men to affordable and quality technical, vocational and tertiary education, including university",
 "4.4"
 "4.4: By 2030, substantially increase the number of youth and adults who have relevant skills, including technical and vocational skills, for employment, decent jobs and entrepreneurship",
 "4.5"
 "4.5: By 2030, eliminate gender disparities in education and ensure equal access to all levels of education and vocational training for the vulnerable, including persons with disabilities, indigenous peoples and children in vulnerable situations",
 "4.6"
 "4.6: By 2030, ensure that all youth and a substantial proportion of adults, both men and women, achieve literacy and numeracy",
 "4.7"
 "4.7: By 2030, ensure that all learners acquire the knowledge and skills needed to promote sustainable development, including, among others, through education for sustainable development and sustainable lifestyles, human rights, gender equality, promotion of a culture of peace and non-violence, global citizenship and appreciation of cultural diversity and of culture’s contribution to sustainable development",
 "4.a"
 "4.a: Build and upgrade education facilities that are child, disability and gender sensitive and provide safe, non-violent, inclusive and effective learning environments for all",
 "4.b"
 "4.b: By 2020, substantially expand globally the number of scholarships available to developing countries, in particular least developed countries, small island developing States and African countries, for enrolment in higher education, including vocational training and information and communications technology, technical, engineering and scientific programmes, in developed countries and other developing countries",
 "4.c"
 "4.c: By 2030, substantially increase the supply of qualified teachers, including through international cooperation for teacher training in developing countries, especially least developed countries and small island developing States",
 "5" "5: Achieve gender equality and empower all women and girls",
 "5.1"
 "5.1: End all forms of discrimination against all women and girls everywhere",
 "5.2"
 "5.2: Eliminate all forms of violence against all women and girls in the public and private spheres, including trafficking and sexual and other types of exploitation",
 "5.3"
 "5.3: Eliminate all harmful practices, such as child, early and forced marriage and female genital mutilation",
 "5.4"
 "5.4: Recognize and value unpaid care and domestic work through the provision of public services, infrastructure and social protection policies and the promotion of shared responsibility within the household and the family as nationally appropriate",
 "5.5"
 "5.5: Ensure women’s full and effective participation and equal opportunities for leadership at all levels of decision-making in political, economic and public life",
 "5.6"
 "5.6: Ensure universal access to sexual and reproductive health and reproductive rights as agreed in accordance with the Programme of Action of the International Conference on Population and Development and the Beijing Platform for Action and the outcome documents of their review conferences",
 "5.a"
 "5.a: Undertake reforms to give women equal rights to economic resources, as well as access to ownership and control over land and other forms of property, financial services, inheritance and natural resources, in accordance with national laws",
 "5.b"
 "5.b: Enhance the use of enabling technology, in particular information and communications technology, to promote the empowerment of women",
 "5.c"
 "5.c: Adopt and strengthen sound policies and enforceable legislation for the promotion of gender equality and the empowerment of all women and girls at all levels",
 "6"
 "6: Ensure availability and sustainable management of water and sanitation for all",
 "6.1"
 "6.1: By 2030, achieve universal and equitable access to safe and affordable drinking water for all",
 "6.2"
 "6.2: By 2030, achieve access to adequate and equitable sanitation and hygiene for all and end open defecation, paying special attention to the needs of women and girls and those in vulnerable situations",
 "6.3"
 "6.3: By 2030, improve water quality by reducing pollution, eliminating dumping and minimizing release of hazardous chemicals and materials, halving the proportion of untreated wastewater and substantially increasing recycling and safe reuse globally",
 "6.4"
 "6.4: By 2030, substantially increase water-use efficiency across all sectors and ensure sustainable withdrawals and supply of freshwater to address water scarcity and substantially reduce the number of people suffering from water scarcity",
 "6.5"
 "6.5: By 2030, implement integrated water resources management at all levels, including through transboundary cooperation as appropriate",
 "6.6"
 "6.6: By 2020, protect and restore water-related ecosystems, including mountains, forests, wetlands, rivers, aquifers and lakes",
 "6.a"
 "6.a: By 2030, expand international cooperation and capacity-building support to developing countries in water- and sanitation-related activities and programmes, including water harvesting, desalination, water efficiency, wastewater treatment, recycling and reuse technologies",
 "6.b"
 "6.b: Support and strengthen the participation of local communities in improving water and sanitation management",
 "7"
 "7: Ensure access to affordable, reliable, sustainable and modern energy for all",
 "7.1"
 "7.1: By 2030, ensure universal access to affordable, reliable and modern energy services",
 "7.2"
 "7.2: By 2030, increase substantially the share of renewable energy in the global energy mix",
 "7.3"
 "7.3: By 2030, double the global rate of improvement in energy efficiency",
 "7.a"
 "7.a: By 2030, enhance international cooperation to facilitate access to clean energy research and technology, including renewable energy, energy efficiency and advanced and cleaner fossil-fuel technology, and promote investment in energy infrastructure and clean energy technology",
 "7.b"
 "7.b: By 2030, expand infrastructure and upgrade technology for supplying modern and sustainable energy services for all in developing countries, in particular least developed countries, small island developing States and landlocked developing countries, in accordance with their respective programmes of support",
 "8"
 "8: Promote sustained, inclusive and sustainable economic growth, full and productive employment and decent work for all",
 "8.1"
 "8.1: Sustain per capita economic growth in accordance with national circumstances and, in particular, at least 7% gross domestic product growth per annum in the least developed countries",
 "8.10"
 "8.10: Strengthen the capacity of domestic financial institutions to encourage and expand access to banking, insurance and financial services for all",
 "8.2"
 "8.2: Achieve higher levels of economic productivity through diversification, technological upgrading and innovation, including through a focus on high-value added and labour-intensive sectors",
 "8.3"
 "8.3: Promote development-oriented policies that support productive activities, decent job creation, entrepreneurship, creativity and innovation, and encourage the formalization and growth of micro-, small- and medium-sized enterprises, including through access to financial services",
 "8.4"
 "8.4: Improve progressively, through 2030, global resource efficiency in consumption and production and endeavour to decouple economic growth from environmental degradation, in accordance with the 10-Year Framework of Programmes on Sustainable Consumption and Production, with developed countries taking the lead",
 "8.5"
 "8.5: By 2030, achieve full and productive employment and decent work for all women and men, including for young people and persons with disabilities, and equal pay for work of equal value",
 "8.6"
 "8.6: By 2020, substantially reduce the proportion of youth not in employment, education or training",
 "8.7"
 "8.7: Take immediate and effective measures to eradicate forced labour, end modern slavery and human trafficking and secure the prohibition and elimination of the worst forms of child labour, including recruitment and use of child soldiers, and by 2025 end child labour in all its forms",
 "8.8"
 "8.8: Protect labour rights and promote safe and secure working environments for all workers, including migrant workers, in particular women migrants, and those in precarious employment",
 "8.9"
 "8.9: By 2030, devise and implement policies to promote sustainable tourism that creates jobs and promotes local culture and products",
 "8.a"
 "8.a: Increase Aid for Trade support for developing countries, in particular least developed countries, including through the Enhanced Integrated Framework for Trade-related Technical Assistance to Least Developed Countries",
 "8.b"
 "8.b: By 2020, develop and operationalize a global strategy for youth employment and implement the Global Jobs Pact of the International Labour Organization",
 "9"
 "9: Build resilient infrastructure, promote inclusive and sustainable industrialization and foster innovation",
 "9.1"
 "9.1: Develop quality, reliable, sustainable and resilient infrastructure, including regional and transborder infrastructure, to support economic development and human well-being, with a focus on affordable and equitable access for all",
 "9.2"
 "9.2: Promote inclusive and sustainable industrialization and, by 2030, significantly raise industry’s share of employment and gross domestic product, in line with national circumstances, and double its share in least developed countries",
 "9.3"
 "9.3: Increase the access of small-scale industrial and other enterprises, in particular in developing countries, to financial services, including affordable credit, and their integration into value chains and markets",
 "9.4"
 "9.4: By 2030, upgrade infrastructure and retrofit industries to make them sustainable, with increased resource-use efficiency and greater adoption of clean and environmentally sound technologies and industrial processes, with all countries taking action in accordance with their respective capabilities",
 "9.5"
 "9.5: Enhance scientific research, upgrade the technological capabilities of industrial sectors in all countries, in particular developing countries, including, by 2030, encouraging innovation and substantially increasing the number of research and development workers per 1 million people and public and private research and development spending",
 "9.a"
 "9.a: Facilitate sustainable and resilient infrastructure development in developing countries through enhanced financial, technological and technical support to African countries, least developed countries, landlocked developing countries and small island developing States",
 "9.b"
 "9.b: Support domestic technology development, research and innovation in developing countries, including by ensuring a conducive policy environment for, inter alia, industrial diversification and value addition to commodities",
 "9.c"
 "9.c: Significantly increase access to information and communications technology and strive to provide universal and affordable access to the Internet in least developed countries by 2020"})


(def default-db {:tab :sdg :id->title id->title})
