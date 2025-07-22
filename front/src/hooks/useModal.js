import { useContext } from 'react';
import { ModalContext } from '../contexts/ModalContext';

export const useModal = () => useContext(ModalContext);
