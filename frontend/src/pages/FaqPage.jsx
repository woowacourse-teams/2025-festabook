import React, { useState, useEffect } from 'react';
import FlipMove from 'react-flip-move';
import { useData } from '../hooks/useData';
import { useModal } from '../hooks/useModal';

const FaqPage = () => {
    const { faqItems, addFaqItem, updateFaqItem, deleteFaqItem, updateFaqSequences, fetchFaqItems } = useData();
    const { openModal, showToast } = useModal();
    const [isEditingOrder, setIsEditingOrder] = useState(false);
    const [tempItems, setTempItems] = useState(faqItems || []);

    // faqItems가 변경될 때 tempItems도 업데이트
    useEffect(() => {
        setTempItems(faqItems || []);
    }, [faqItems]);

    // 컴포넌트 마운트 시 FAQ 데이터 새로 조회
    useEffect(() => {
        fetchFaqItems();
    }, []); // eslint-disable-line react-hooks/exhaustive-deps

    const handleSave = async (id, data) => {
        if (!data.question || !data.answer) { showToast('질문과 답변을 모두 입력해주세요.'); return; }
        try {
            if (id) {
                await updateFaqItem(id, data, showToast);
            } else {
                await addFaqItem(data, showToast);
            }
        } catch {
            showToast('FAQ 저장 중 오류가 발생했습니다.');
        }
    };

    const handleReorder = (index, direction) => {
        const newItems = [...tempItems];
        const newIndex = direction === 'up' ? index - 1 : index + 1;
        if (newIndex < 0 || newIndex >= newItems.length) return;
        [newItems[index], newItems[newIndex]] = [newItems[newIndex], newItems[index]];
        setTempItems(newItems);
    };

    const handleSaveOrder = async () => {
        const sequences = tempItems.map((item, index) => ({
            questionId: item.questionId,
            sequence: index + 1
        }));
        await updateFaqSequences(sequences, showToast);
        setIsEditingOrder(false);
    };

    const handleCancelOrder = () => {
        setTempItems(faqItems || []);
        setIsEditingOrder(false);
    };

    return (
        <div>
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-3xl font-bold">FAQ 관리</h2>
                <div className="flex items-center space-x-2">
                    {isEditingOrder ? (
                        <>
                            <button onClick={handleCancelOrder} className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-lg">취소</button>
                            <button onClick={handleSaveOrder} className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg">순서 저장</button>
                        </>
                    ) : (
                        <>
                            <button onClick={() => setIsEditingOrder(true)} className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-lg">순서 변경</button>
                            <button onClick={() => openModal('faq', { onSave: (data) => handleSave(null, data) })} className="bg-gray-800 hover:bg-gray-900 text-white font-bold py-2 px-4 rounded-lg flex items-center"><i className="fas fa-plus mr-2"></i> FAQ 등록</button>
                        </>
                    )}
                </div>
            </div>
            <FlipMove className="space-y-4">
                {(isEditingOrder ? tempItems : faqItems || []).map((item, index) => (
                    <div key={item.questionId} data-id={item.questionId} className="bg-white rounded-lg shadow-sm border border-gray-200 p-5">
                        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2">
                            <p className="font-semibold flex-1 truncate" title={item.question}><span className="text-blue-600 mr-2">Q.</span>{item.question}</p>
                            <div className="flex items-center space-x-3 ml-0 sm:ml-4 flex-wrap">
                                {isEditingOrder ? (
                                    <>
                                        <button onClick={() => handleReorder(index, 'up')} disabled={index === 0} className="text-gray-400 hover:text-gray-600 disabled:opacity-50"><i className="fas fa-arrow-up"></i></button>
                                        <button onClick={() => handleReorder(index, 'down')} disabled={index === tempItems.length - 1} className="text-gray-400 hover:text-gray-600 disabled:opacity-50"><i className="fas fa-arrow-down"></i></button>
                                    </>
                                ) : (
                                    <>
                                        <button
                                            onClick={() => {
                                                openModal('confirm', {
                                                title: 'FAQ 삭제 확인',
                                                message: `'${item.question}' FAQ를 정말 삭제하시겠습니까?`,
                                                onConfirm: async () => {
                                                    await deleteFaqItem(item.questionId, showToast);
                                                }
                                                });
                                            }}
                                            className="text-red-600 hover:text-red-800 font-bold"
                                        >
                                            삭제
                                        </button>
                                    </>
                                )}
                            </div>
                        </div>
                        <div className="mt-4 pl-8 border-l-2 border-gray-200 ml-1"><p className="text-gray-700"><span className="font-bold text-gray-500 mr-2">A.</span>{item.answer}</p></div>
                    </div>
                ))}
                
                {/* FAQ가 없을 때 */}
                {(!isEditingOrder && (!faqItems || faqItems.length === 0)) && (
                    <div className="text-center py-12">
                        <i className="fas fa-question-circle text-4xl text-gray-400 mb-4"></i>
                        <p className="text-gray-500 mb-4">등록된 FAQ가 없습니다</p>
                        <button
                            onClick={() => openModal('faq', { onSave: (data) => handleSave(null, data) })}
                            className="bg-gray-800 hover:bg-gray-900 text-white px-4 py-2 rounded-lg transition-colors"
                        >
                            첫 번째 FAQ 등록
                        </button>
                    </div>
                )}
            </FlipMove>
        </div>
    );
};

export default FaqPage;
