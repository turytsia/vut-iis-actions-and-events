import React, { useState } from 'react'
import classes from "./CreatePlaceModal.module.css"
import Modal, { ModalStyles } from '../../../../../../components/Modal/Modal'
import { PlaceType } from '../../Places'
import Input from '../../../../../../components/Input/Input'
import icons from '../../../../../../utils/icons'
import { CategoryType } from '../../../Categories/Categories'

const initialInputs: PlaceType = {
    "id": -1,
    "name": "",
    "description": "",
    "address": "",
    "status": "APPROVED"
}

type PropsType = {
    inputs?: PlaceType
    places: PlaceType[]
    onSubmit: (inputs: PlaceType) => void
    onClose: () => void
    title: string,
    textProceed: string
}

const CreatePlaceModal = ({
    inputs: defaultInputs,
    places,
    onSubmit,
    onClose,
    title,
    textProceed
}: PropsType) => {
    const [inputs, setInputs] = useState(defaultInputs ?? initialInputs)

    const onChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setInputs(prev => ({ ...prev, [e.target.name]: e.target.value }))
    }

    const __onSubmit = () => {
        onSubmit(inputs)
    }
    
  return (
      <Modal
          title={title}
          textProceed={textProceed}
          textCancel={'Cancel'}
          onClose={onClose}
          onSubmit={__onSubmit}
          icon={icons.pen}
          type={ModalStyles.Inputs}>
          <Input label='Name' name='name' value={inputs.name} onChange={onChange} />
          <Input label='Description' name='description' value={inputs.description} onChange={onChange} />
          <Input label='Address' name='address' value={inputs.address} onChange={onChange} />
      </Modal>
  )
}

export default CreatePlaceModal