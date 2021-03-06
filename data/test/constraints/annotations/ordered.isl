type::{
  annotations: ordered::[a, b, c, d],
}
valid::[
  5,
  a::5,
  b::5,
  c::5,
  d::5,
  a::b::5,
  a::c::5,
  a::d::5,
  b::d::5,
  c::d::5,
  a::b::c::5,
  a::b::c::d::5,
  open_content::a::open_content::b::open_content::c::open_content::d::open_content::5,
]
invalid::[
  b::a::c::d::5,
  a::c::b::d::5,
  a::b::d::c::5,
  d::c::b::a::5,
  open_content::d::open_content::c::open_content::b::open_content::a::open_content::5,
]

type::{
  annotations: ordered::required::[a, b, c, d],
}
valid::[
  a::b::c::d::5,
  open_content::a::open_content::b::open_content::c::open_content::d::open_content::5,
]
invalid::[
  b::c::d::5,
  a::c::d::5,
  a::b::d::5,
  a::b::c::5,
]

// same as previous, with 'required' and 'ordered' annotations swapped
type::{
  annotations: required::ordered::[a, b, c, d],
}
valid::[
  a::b::c::d::5,
  open_content::a::open_content::b::open_content::c::open_content::d::open_content::5,
]
invalid::[
  b::c::d::5,
  a::c::d::5,
  a::b::d::5,
  a::b::c::5,
]

type::{
  annotations: ordered::optional::[a, required::b, c, required::d],
}
valid::[
     b::   d::5,
  a::b::   d::5,
     b::c::d::5,
  a::b::c::d::5,
]
invalid::[
  d::b::5,
  b::a::d::5,
  c::b::d::5,
  c::b::a::d::5,
]
